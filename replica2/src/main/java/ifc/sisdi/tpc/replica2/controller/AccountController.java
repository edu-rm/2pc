package ifc.sisdi.tpc.replica2.controller;

import ifc.sisdi.tpc.replica2.exception.NoException;
import ifc.sisdi.tpc.replica2.models.Account;
import ifc.sisdi.tpc.replica2.models.Action;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Random;

@RestController
@RequestMapping("/conta")
public class AccountController {
    private ArrayList<Account> accounts = new ArrayList<>();
    private ArrayList<Action> log = new ArrayList<Action>();

    public AccountController(){
        accounts.add(new Account(1234,100.00));
        accounts.add(new Account(4345,50.00));
        accounts.add(new Account(5678,250.00));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Action sendAction(Action action){
    	System.out.println("Replica 2: ação enviada "+ action.getId());
    	Random r = new Random();
        int error = r.nextInt(10);

        if(error > 6){
            throw new NoException();
        }
        
        boolean achou = false;
        for(Account account : this.accounts){
            if(account.getNumber() == action.getConta()){
//                switch (action.getOperacao()) {
//                    case "debito" -> account.setBalance(account.getBalance() + action.getValor());
//                    case "credito" -> account.setBalance(account.getBalance() - action.getValor());
//                }
                
                achou = true;
            }
        }
        
        if (!achou) {
        	throw new NoException();
        }
        this.log.add(action);
    	System.out.println(this.log);
 
        return action;
    }
    
    @PutMapping("/commit")
    @ResponseStatus(HttpStatus.CREATED)
    public Action commit(int id){
    	System.out.println("Replica 2: commitando: "+ id);
    	Action actionAux = null;
    	 
//    	Acha a ação e logo depois vê se ela existe
    	int indexAction = 0;
    	int counter = 0;
    	
    	for(Action actionMap : this.log){
            if(actionMap.getId() == id){
            	actionAux = actionMap;
            	indexAction = counter;
            }
            counter++;
        }
    	
    	if (actionAux == null) {
    		throw new NoException();
    	}
    	
//    	Hora de commitar a ação, tentando achar a conta que está solicitada na ação e comitando
    	boolean achou = false;
        for(Account account:this.accounts){
            if(account.getNumber() == actionAux.getConta()){
            	if (actionAux.getOperacao() == "debito") {
            		account.setBalance(account.getBalance() + actionAux.getValor());
            	} else {
            		account.setBalance(account.getBalance() - actionAux.getValor());
            	}
                
                achou = true;
                this.log.remove(indexAction);

            }
        }
        
    	System.out.println(this.log);

//        if (!achou) {
//        	throw new NoException();
//        }
        return actionAux;
    }
    
    @DeleteMapping("/commit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void rollback(int id){
    	System.out.println("Replica 2: dando rollback: "+ id);

    	int indexAction = 0;
    	int counter = 0;
    	boolean achou = false;
    	for(Action actionMap : this.log){
            if(actionMap.getId() == id){
            	indexAction = counter;
            	achou = true;
            }
            counter++;
        }
    	System.out.println(this.log);
    	this.log.remove(indexAction);

    	if (!achou) {
    		throw new NoException();
    	}
    	
    }
    
    @ControllerAdvice
    static class Fail {
        @ResponseBody
        @ExceptionHandler(NoException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        String no(NoException n) {
            return n.getMessage();
        }
    }
}
