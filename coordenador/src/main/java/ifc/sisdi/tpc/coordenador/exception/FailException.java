package ifc.sisdi.tpc.coordenador.exception;

public class FailException extends RuntimeException {

    public FailException() {
        super("2PC MESSAGE:FAIL!");
    }
}

