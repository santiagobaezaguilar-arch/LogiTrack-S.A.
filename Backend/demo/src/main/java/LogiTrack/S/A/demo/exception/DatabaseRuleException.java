package LogiTrack.S.A.demo.exception;

public class DatabaseRuleException extends RuntimeException {
    public DatabaseRuleException(String mensaje) {
        super(mensaje);
    }
}
