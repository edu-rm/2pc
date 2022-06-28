package ifc.sisdi.tpc.coordenador.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(int account) {
        super("Conta não encontrada: " + account);
    }
}
