package openmods.whodunit.data;

public class CallMarker extends RuntimeException {
    private static final long serialVersionUID = 7568556360854585129L;

    public final int location;

    public CallMarker(int location) {
        this.location = location;
    }
}
