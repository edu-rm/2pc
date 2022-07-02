package ifc.sisdi.tpc.coordenador.controller;

import ifc.sisdi.tpc.coordenador.exception.AccountNotFoundException;
import ifc.sisdi.tpc.coordenador.exception.FailException;
import ifc.sisdi.tpc.coordenador.model.Action;
import ifc.sisdi.tpc.coordenador.model.Conta;
import ifc.sisdi.tpc.coordenador.model.Replica;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class AccountController {
    private AtomicInteger counter = new AtomicInteger();
    private ArrayList<Conta> accounts = new ArrayList<Conta>();
    private ArrayList<Action> log = new ArrayList<Action>();

    private ArrayList<Replica> replicas = new ArrayList<Replica>();
    HttpClient client = HttpClient.newHttpClient();

    public AccountController() {
        this.accounts.add(new Conta(1234, 100.00));
        this.accounts.add(new Conta(4345, 50.00));
        this.accounts.add(new Conta(5678, 250.00));

        this.replicas.add(new Replica(1, "http://localhost:8081"));
        this.replicas.add(new Replica(2, "http://localhost:8082"));
    }

    @GetMapping("/contas")
    public ArrayList<Conta> getAccounts() {
        return this.accounts;
    }

    @GetMapping("/replicas")
    public ArrayList<Replica> getReplicas() {
        return this.replicas;
    }

    @PostMapping("/acao")
    public Action sendAction(@RequestBody Action action) throws IOException, InterruptedException {
        for(Conta account : this.accounts) {
            if(account.getNumero() == action.getConta()) {
            	int id = counter.getAndIncrement();
                action.setId(id);
                log.add(action);

                Map<Object,Object> data = new HashMap<>();

                data.put("id", id);
                data.put("conta", action.getConta());
                data.put("valor", action.getValor());
                data.put("operacao", action.getOperacao());
                boolean hasError = false;
                for(Replica replica : this.replicas) {
                    System.out.println(replica.getHost());
                    HttpRequest request = HttpRequest.newBuilder().POST(buildFormDataFromMap(data))
                            .uri(URI.create(replica.getHost()+ "/conta" + "?id=" + id + "&" + "conta="+ action.getConta() + "&valor=" + action.getValor() + "&operacao=" + action.getOperacao())).build();
                    HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.statusCode());
                    System.out.println(response.body());
                    if (response.statusCode() >= 400) {
                    	hasError = true;
                    }
                }
                
                if (hasError) {
                	for(Replica replica : this.replicas) {
                        System.out.println(replica.getHost());
                        HttpRequest request = HttpRequest.newBuilder().DELETE()
                                .uri(URI.create(replica.getHost()+ "/conta/commit" + "?id=" + id )).build();
                        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
                        System.out.println(response.statusCode());
                        System.out.println(response.body());
                	}
                	
                	throw new FailException(); 
                }
                
                // comita a ação em ambos
                
                for(Replica replica : this.replicas) {
                    System.out.println(replica.getHost());
                    HttpRequest request = HttpRequest.newBuilder().PUT(buildFormDataFromMap(data))
                            .uri(URI.create(replica.getHost()+ "/conta/commit" + "?id=" + id )).build();
                    HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.statusCode());
                    System.out.println(response.body());
                    if (response.statusCode() >= 400) {
                        throw new FailException();
                    }
                }
                
                // Remove da memória temporária e faz a operação
                log.remove(action);

                if (action.getOperacao() == "debito") {
                	account.setSaldo(account.getSaldo() - action.getValor());
                } else {
                	account.setSaldo(account.getSaldo() + action.getValor());
                }
                
                return action;
            }
        }
        throw  new AccountNotFoundException(action.getConta());
    }
    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
     
    

    @ControllerAdvice
    static class Fail {
        @ResponseBody
        @ExceptionHandler(FailException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        String fail(FailException f) {
            return f.getMessage();
        }
    }

    @ControllerAdvice
    static class AccountNotFound {
        @ResponseBody
        @ExceptionHandler(AccountNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String fail(AccountNotFoundException c) {
            return c.getMessage();
        }
    }
}
