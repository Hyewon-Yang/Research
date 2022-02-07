import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author seungeun
 */
public class Track {
    int track_id;
    ArrayList<Community> community;
    ArrayList<Agent> ppl;
    int numOfppl, numOfCommunity;
    Disease disease;

    public Track(){
        initialize();
    }

    public void add_community(Community new_com){
        new_com.community_id = numOfCommunity;
        community.add(new_com);
        numOfCommunity++;
    }

    public void add_agent(Agent new_agent){
        ppl.add(new_agent);
    }

    // remove ppl during day/night time for next day/night schedule
    public void shift(){
        ppl = new ArrayList<Agent>(); //population generates
        Iterator iter = community.iterator();

        while(iter.hasNext()){
            Community c = (Community)iter.next();
            c.c_time += 0.5;
            c.shift();
        }
    }

    // update per day
    public void disease_update(int day_type, int scenario_num, int bubble_size_now, double accepted_range_now){
        Iterator iter = community.iterator();
        while(iter.hasNext()){
            Community c = (Community)iter.next();
            c.disease_update(day_type, scenario_num, bubble_size_now, accepted_range_now);
        }
    }

    private void initialize(){
        community = new ArrayList<Community>();
        ppl = new ArrayList<Agent>();
        disease = new Disease();
        numOfppl = numOfCommunity = 0;
    }
}