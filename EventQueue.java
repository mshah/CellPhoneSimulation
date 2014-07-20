import java.util.PriorityQueue;

public class EventQueue{

    public EventQueue(){
    }

    // accessors
    public void SetQueue(PriorityQueue<Event> inQueue){
        fQueue = inQueue;
    }

    public void AddEvent(Event inEvent){
        fQueue.add(inEvent);
    }

    public Event GetEvent(){
        Event outEvent = fQueue.poll();
        return outEvent;
    }

    public boolean RemoveEvent(Event inEvent){
        return fQueue.remove(inEvent);
    }

    public boolean IsEmpty(){
        return fQueue.isEmpty();
    }

    private PriorityQueue<Event> fQueue;

}