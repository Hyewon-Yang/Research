import java.util.ArrayList;
import java.util.Iterator;

public class Neighborhood {
    int neighbor_id;
    ArrayList<Agent> ppl;
    Disease disease;
    Main m;

    public Neighborhood(){
        initialize();
    }

    private void initialize(){
        ppl = new ArrayList<Agent>();
        disease = new Disease();
        m = new Main();
    }
    // remove ppl during day/night time for next day/night schedule
    public void shift(){
        ppl = new ArrayList<Agent>();
        Iterator iter;
    }

    public void add_agent(Agent new_agent){
        ppl.add(new_agent);
    }
   
    public void disease_update(int day_type, int scenario_num_now, int bubble_size_now, double accepted_range_now){
        Iterator iter = ppl.iterator();
        ArrayList<Agent> s_group = new ArrayList<Agent>();
        ArrayList<Agent> i_group = new ArrayList<Agent>();

        while(iter.hasNext()){
            Agent next = (Agent)iter.next();
            double r = Math.random();
            if(next.status==0 && next.infected_today == false){
                s_group.add(next);
            }else if(next.status<=2 && next.infected_today == false){
                i_group.add(next);
            }
        }

        Iterator iter_i = i_group.iterator();

        while(iter_i.hasNext()){
            Agent infected = (Agent)iter_i.next();
            double infected_prob, contact_prob;
            Iterator iter_s = s_group.iterator();

            while(iter_s.hasNext()){
                if(infected.day_type !=1 && day_type != 1){
                    Agent susceptible = (Agent)iter_s.next();
                    if(susceptible.is_same_neighbor(infected, day_type) && susceptible.status == 0){
                        if(susceptible.PA_satisfied_s12 == true || susceptible.PA_satisfied_s3 == true || susceptible.PA_satisfied_s5 == true){
                            contact_prob = disease.calculate_contact_prob_revised(susceptible.age_type, infected.age_type, 8, scenario_num_now, bubble_size_now, accepted_range_now);
                        }
                        else{
                            contact_prob = disease.calculate_contact_prob(susceptible.age_type, infected.age_type, 8);
                        }
                        double q = Math.random();
                        if (q < contact_prob && susceptible.status == 0){
                            susceptible.contact_location = 8;

                            infected_prob = disease.p_trans;
                            //infected_prob = contact_prob * disease.p_trans;
                            infected_prob = infected_prob * susceptible.age_dependent_susceptibility(susceptible.age_type);
                                
                            double r = Math.random();
                            if(r < infected_prob && susceptible.status == 0){
                                    susceptible.infected_period = 1;
                                    susceptible.infected_today = true;
                                    susceptible.infectiousness = 1; 
                                    susceptible.infected_neighborhood = true;
                                    susceptible.infection_location = 8;
                            }
                        }
                    }
                }
            }
        }
    }
}
