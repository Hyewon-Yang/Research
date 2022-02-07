import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * 가장 최소 단위의 장소: basic unit of the location or building
 * @author seungeun
 */
public class Spot {
    int spot_id, spot_type, max_pplNum;
    int occupied;
    //int numOfInfection, numOfNewInfection;
    /*
     * spot type: 
     household(0), household cluster(1), 
     playgroup(2), daycare(3)
     elementary school(4), middle school(5), high school(6),
     workgroup(7),
     day_household(8), community(9)
     bed(10)
     hospital(11)
     Treatment center (12)
     Treatment center cluster (13)
     publicly used facilities (14)
     General hospital (15)
     General hospital cluster (16)
     Bubble_small (17), medium (18), large (19)
     */
    ArrayList<Agent> ppl;
    ArrayList<Spot> homes;
    ArrayList<Spot> bubbles_small, bubbles_medium, bubbles_large;
    Disease disease;
    Main m;

    
    public Spot(int type){                
        spot_type = type;
        initialize();
    }
    
    // remove ppl during day/night time for next day/night schedule
    public void shift(int spot_type){
        ppl = new ArrayList<Agent>();
        //household cluster일때 shift
        if(spot_type == 1){
            for(int i=0; i < homes.size(); i++)
            {
                Spot home = homes.get(i);
                home.shift(0);
            }
        }
    }

    public boolean add_agent(Agent new_agent){
        if(ppl.size() < max_pplNum){
            ppl.add(new_agent);
            return true;
        }
        else{
            return false;
        }
    }

    public void disease_update(int type, int day_type, int scenario_num_now, int bubble_size_now, double accepted_range_now){
        if(!ppl.isEmpty()){
            Iterator iter = ppl.iterator();
            ArrayList<Agent> s_group = new ArrayList<Agent>();
            ArrayList<Agent> i_group = new ArrayList<Agent>();

            while(iter.hasNext()){
                Agent next = (Agent)iter.next();
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
                    Agent susceptible = (Agent)iter_s.next();
                    //not household
                    if(type >= 1){
                        if(infected.bed_type == 1){
                            infected.day_neighbor = infected.night_neighbor;
                            infected.day_cluster = infected.night_cluster;
                            infected.day_spot = infected.night_spot;
                            infected.day_type = 0;
                        }

                        if(susceptible.infected_period == 0 && susceptible.is_same_spot(infected, day_type) && infected.bed_type<=1 && susceptible.status == 0){
                            if(susceptible.PA_satisfied_s12 == true || susceptible.PA_satisfied_s3 == true || susceptible.PA_satisfied_s5 == true){
                                contact_prob = disease.calculate_contact_prob_revised(susceptible.age_type, infected.age_type, type, scenario_num_now, bubble_size_now, accepted_range_now);
                            }
                            else{
                                contact_prob = disease.calculate_contact_prob(susceptible.age_type, infected.age_type, type);
                            }
                            double q = Math.random();
                            if (q < contact_prob && susceptible.status == 0){
                                susceptible.contact_location = type;

                                infected_prob = disease.p_trans;
                                // infected_prob = contact_prob * disease.p_trans;
                                infected_prob = infected_prob * susceptible.age_dependent_susceptibility(susceptible.age_type);

                                double r = Math.random();
                                if(r < infected_prob && susceptible.status == 0){
                                    susceptible.infected_period = 1;
                                    susceptible.infected_today = true;
                                    susceptible.infectiousness = 1;
                                    susceptible.infected_spot = true;
                                    susceptible.infection_location = type;
                                }
                            }
                        }

                    }else{ // type=0(household), 자가격리중인 감염자와 만나는 경우 현재는 다르진 않음 향후 자가격리 키트??
                        if(susceptible.infected_period == 0 && susceptible.is_same_spot(infected, day_type) && infected.bed_type<=1 && susceptible.status == 0){
                            if(susceptible.PA_satisfied_s12 == true || susceptible.PA_satisfied_s3 == true || susceptible.PA_satisfied_s5 == true){
                                contact_prob = disease.calculate_contact_prob_revised(susceptible.age_type, infected.age_type, type, scenario_num_now, bubble_size_now, accepted_range_now);
                            }
                            else{
                                contact_prob = disease.calculate_contact_prob(susceptible.age_type, infected.age_type, type);
                            }
                            double q = Math.random();
                            if (q < contact_prob && susceptible.status == 0){
                                susceptible.contact_location = type;

                                infected_prob = disease.p_trans;
                                // infected_prob = contact_prob * disease.p_trans;
                                infected_prob = infected_prob * susceptible.age_dependent_susceptibility(susceptible.age_type);

                                double r = Math.random();
                                if(r < infected_prob && susceptible.status == 0){
                                    susceptible.infected_period = 1;
                                    susceptible.infected_today = true;
                                    susceptible.infectiousness = 1;
                                    susceptible.infected_spot = true;
                                    susceptible.infection_location = type;
                                }
                            }
                        }
                    }
                }
            }

            // hosehold disease update (가족 감염)
            if(type == 1){
                iter = homes.iterator();
                while(iter.hasNext()){
                    Spot s = (Spot)iter.next();
                    s.disease_update(0, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
                }
            }
        }
    }

    private void initialize(){
        occupied = 0;
        homes = new ArrayList<Spot>();
        bubbles_small = new ArrayList<Spot>();
        bubbles_medium = new ArrayList<Spot>();
        bubbles_large = new ArrayList<Spot>();
        ppl = new ArrayList<Agent>();
        disease = new Disease();
        m = new Main();
        switch(spot_type){
            case 0: max_pplNum = 7; break; //household
            case 1: max_pplNum = 28; break; //household cluster
            case 2: max_pplNum = 4; break; //playgroup
            case 3: max_pplNum = 14; break; //daycare
            case 4: max_pplNum = 79; break; //elementary
            case 5: max_pplNum = 128; break; //middle
            case 6: max_pplNum = 155; break; //high
            case 7: max_pplNum = 20; break; //workgroup
            case 8: break; //neighborhood? max_pplNum = 28; 
            case 9:
                System.out.println("There is an error: neighborhood/community isn't spot");
                break;
            case 10: max_pplNum = 1; break; // Bed
//            case 11: max_pplNum = 999; break; // Hospital
            case 12: max_pplNum = 200; break; // Treatment center
//            case 13: max_pplNum = 999; break; // Treatment center cluster
            case 14: max_pplNum = 5000; break; // publicly used facilities
            case 15: max_pplNum = 100000; break; // Genbed 갯수
//            case 16: max_pplNum = ; break; // General hospital cluster
            case 17: max_pplNum = 10000; break; // Small bubbles
            case 18: max_pplNum = 10000; break; // Medium bubbles
            case 19: max_pplNum = 10000; break; // Large bubbles
            default:
                System.out.println("There is an type error: in Neighborhood");
        }   
    }

    public void add_home(Spot h){
        homes.add(h);
    }

    public void add_home_in_bubble(Spot h, int i){
        switch(i){
            case 0: bubbles_small.add(h); break;
            case 1: bubbles_medium.add(h); break;
            case 2: bubbles_large.add(h); break;
            default:
                System.out.println("add home error");          
        }
    }
}
