package ifc.sisdi.tpc.replica2.exception;

@SuppressWarnings("serial")
public class NoException extends RuntimeException {
    public NoException() {
        super("Response 2PC: no");
    }
}
