enum EventType{
    kEventCallAttempt,
    kEventCallDisconnect
}

public class Event{

    public Event(){
        fNumber = 0;
        fTime = 0;
        fCell = 0;
        fDuration = 0;
        fType = EventType.kEventCallAttempt;
    }

    public Event(int inNumber, int inTime, int inCell, int inDuration, EventType inType){
        fNumber = inNumber;
        fTime = inTime;
        fCell = inCell;
        fDuration = inDuration;
        fType = inType;
    }

    public Event(int inNumber, int inTime, int inCell){
        fNumber = inNumber;
        fTime = inTime;
        fCell = inCell;
        fType = EventType.kEventCallDisconnect;
    }

    // accessors
    public int      GetNumber(){ return fNumber; }
    public void     SetNumber(int inNumber){ fNumber = inNumber; }

    public int      GetTime(){ return fTime; }
    public void     SetTime(int inTime){ fNumber = inTime; }

    public int      GetCell(){ return fCell; }
    public void     SetCell(int inCell){ fCell = inCell; }

    public int      GetDuration(){ return fDuration; }
    public void     SetDuration(int inDuration){ fDuration = inDuration; }

    public EventType GetType(){ return fType; }
    public void      SetType(EventType inType){ fType = inType; }

    // private
    private int fNumber;
    private int fTime;
    private int fCell;
    private int fDuration;
    private EventType fType;
}