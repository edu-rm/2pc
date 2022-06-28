package ifc.sisdi.tpc.coordenador.model;

public class Replica {
    private int id;
    private String host;

    public Replica(int id, String host) {
        this.id = id;
        this.host = host;
    }

    public int getId() {
        return id;
    }

    public String getHost() {

        return host;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }


}
