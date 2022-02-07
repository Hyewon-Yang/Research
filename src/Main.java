import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Main {
    static int numOfppl_ = 191774;              //191774 : 서울 2%  , 153821 : census size, 1999 : short census size
    static int run = 10;                         //replication
    static int runtime = 500;     //500              //period of infection
    static String filename =            
    "/census_seoul_new.txt";                    // new census data

    ArrayList<Track> dj; 
    static ArrayList<Agent> people; 
    static ArrayList<ArrayList<Agent>> group0_, group1_, group2_, group3_, group4_;
    Window window;
    int numOfppl, numOfTrack;
    static int census_max_family_id;

    //scenario setting
    //static int scenario_num = 1;
    static int scenario_num[] = {2}; //multiple values are possible
    static double policy_start_time[] = {0}; //multiple values are possible
    //static int start_time = 50; //same with second value in policy_start_time
    int scenario_num_now;

    //scenario 1 & 2
    static int bubble_size[] = {4}; //multiple values are possible, default value 0
    //static int policy_stringency[] = {3}; //weak(1), medium(2), strong(3)
    static ArrayList<Integer> weekly_infection;
    static double previous_PA;
    int bubble_size_now;
    double PA_level;

    //scenario 3
    boolean is_scenario_3 = false;

    //scenario 4 = OFF all scenarios

    //scenario 5
    boolean is_scenario_5 = false;
    static double accepted_range[] = {1.0}; //multiple values are possible, default value 1.0
    double accepted_range_now;
    //set policy_start_time above


    // simulation output
    int numOfInfection, numOfNewInfection, numOfNewRecovered, numOfRecovered, initial_Seed, numOfDead, numOfNewDead, numOfSevere, numOfNewSevere, numOfReported, numOfNewReported, numOfQuarantine, numOfNewQuarantine;
    int[] status_count, infection_location_count, contact_location_count;
    int daily_PA_unsatisfied, daily_PA_satisfied;
    int initial_id;
    double time;
    Random rnd;
    int numOfCommunity;
    int extra; int extra_Seed;
    
    public void initialize(int numOfppl_) throws FileNotFoundException, IOException{
        numOfppl = numOfppl_;
        numOfTrack = 1;
        numOfCommunity = 1;
        numOfInfection = numOfNewInfection = numOfNewRecovered = numOfRecovered = numOfDead = numOfNewDead = numOfSevere = numOfNewSevere = numOfNewReported = numOfReported = numOfQuarantine = numOfNewQuarantine = 0 ;
        census_max_family_id = 0;
        status_count = new int[11];
        infection_location_count = new int[21];
        contact_location_count = new int[21];
        initial_Seed = 0;                      
        time = 0.0;
        extra = 0;
        dj = new ArrayList<Track>();
        people = new ArrayList<Agent>();        //전체 인구
        weekly_infection = new ArrayList<Integer>(Collections.nCopies(7, 0));
        daily_PA_unsatisfied = daily_PA_satisfied = 0;
        previous_PA = 0.6; //initial PA

        //Age groups
        group0_ = new ArrayList<ArrayList<Agent>>();
        group1_ = new ArrayList<ArrayList<Agent>>();
        group2_ = new ArrayList<ArrayList<Agent>>();
        group3_ = new ArrayList<ArrayList<Agent>>();
        group4_ = new ArrayList<ArrayList<Agent>>();
        
        int random_seed = 3166;
        
        rnd = new Random(random_seed);

        //Population generation
        generate_step_1();
        generate_step_2();      // Census data 반영

        numOfppl = people.size();
        System.out.println(numOfppl);

        // Initial seed 할당 (해외유입 할거면 의미없어서 주석처리함)
        int nOfSeed = 1;    // 첫 날 넣어주는 initial seed
        for(int i=0; i < nOfSeed; i++){
            initial_id = rnd.nextInt(numOfppl);
            initial_Seed = initial_id;
            // System.out.println("initial_Seed " + initial_id);
            // System.out.println("initial_Seed_ID " + people.get(initial_id).age_type + " " + people.get(initial_id).day_type + " " + people.get(initial_id).day_neighbor);
            people.get(initial_id).infected_period = 1;
            people.get(initial_id).isInitialSeed = true;
            people.get(initial_id).status = 1;
            people.get(initial_id).infected_today = true;
            people.get(initial_id).incubation_period = 50;
            people.get(initial_id).recovery_period = 50;
            people.get(initial_id).infectiousness = 1; //initial setting
        }
    }

    //update per day
    public void disease_update(){
        for(int i = 0; i < dj.size(); i++){
            //Community c;
            int bubble_size_val;
            double policy_start_time_val;
            double accepted_range_val;
            int scenario_num_val;

            //daily outputs
            numOfNewInfection = 0;
            numOfNewRecovered = 0;
            numOfNewDead = 0;
            numOfNewSevere = 0;
            numOfNewReported = 0;
            numOfNewQuarantine = 0;

            bubble_size_val = 0;
            policy_start_time_val = 0;
            accepted_range_val = 0;
            scenario_num_val = 0;
            daily_PA_satisfied = 0;
            daily_PA_unsatisfied = 0;

            //daytime 스케줄 변경
            dj.get(i).shift();      // time shift
            for(int j = 0; j < policy_start_time.length; j++){
                if(policy_start_time.length == 1){
                    bubble_size_val = bubble_size[0];
                    accepted_range_val = accepted_range[0];
                    policy_start_time_val = policy_start_time[0];
                    scenario_num_val = scenario_num[0];
                    break;
                }else{
                    if(policy_start_time[j] <= time && time < policy_start_time[j+1]){
                        bubble_size_val = bubble_size[j];
                        accepted_range_val = accepted_range[j];
                        policy_start_time_val = policy_start_time[j];
                        scenario_num_val = scenario_num[j];
                        break;
                    }else if(policy_start_time[policy_start_time.length - 1] <= time){
                        bubble_size_val = bubble_size[bubble_size.length - 1];
                        accepted_range_val = accepted_range[accepted_range.length - 1];
                        policy_start_time_val = policy_start_time[policy_start_time.length - 1];
                        scenario_num_val = scenario_num[scenario_num.length - 1];
                        break;
                    }
                }
            }

            System.out.println("Bubble size: " + bubble_size_val);
            System.out.println("Accepted_range: " + accepted_range_val);
            System.out.println("Policy start time: " + policy_start_time_val);
            System.out.println("Scenario num: " + scenario_num_val);
            bubble_size_now = bubble_size_val;
            accepted_range_now = accepted_range_val;
            scenario_num_now = scenario_num_val;
            PA_level = calculate_policy_adherence(scenario_num_val, bubble_size_val, accepted_range_val, policy_start_time_val);
            //PA_level = previous_PA;
            System.out.println("PA level: " + PA_level);
            move_day(PA_level, scenario_num_val, bubble_size_val, accepted_range_val);

            //nighttime 스케줄 변경
            dj.get(i).shift();      // time shift
            move_night();

            //total outputs update
            numOfInfection += numOfNewInfection;
            numOfRecovered += numOfNewRecovered;
            numOfDead += numOfNewDead;
            numOfSevere += numOfNewSevere;
            numOfReported += numOfNewReported;
            numOfQuarantine += numOfNewQuarantine;

            previous_PA = PA_level;
            weekly_infection.remove(0);
            weekly_infection.add(numOfNewInfection);

            System.out.println("weekly infection: " + weekly_infection);

            System.out.println("New Infection : "+numOfNewInfection);
            System.out.println("S = "+(numOfppl-numOfInfection));
            System.out.println("I = "+(numOfInfection-numOfRecovered-numOfDead));
            System.out.println("R = "+(numOfRecovered+numOfDead));
            System.out.println("PA satisfied : "+daily_PA_satisfied);
        }
    }

    // assign a people in each track, community, neighborhood, spot
    // according to his/her predefined daytime schedule
    private void move_day(double PA_level, int scenario_num_now, int bubble_size_now, double accepted_range_now){
        for(int i=0; i < numOfppl; i++){
            Agent person = people.get(i);
            int d_t = person.day_type;
            int d_s = person.day_spot;
            Community c = dj.get(0).community.get(person.day_com); // 
            Spot s;
            Spot s_daycluster;

            if(d_t == 0 | d_t == 10  | d_t == 12 | d_t == 15 ){ // day type과 night type이 같은 경우
                c =  dj.get(0).community.get(person.night_com); // 
            }
            c.add_agent(person);
            Neighborhood n = c.neighbor.get(person.day_neighbor);
            n.add_agent(person);
            
            //Bubble moving condition(with calculated PA level) -- Scenario 1 & 2
            if(person.status <= 2 && bubble_size_now > 0){
                if(PA_level == 1.0){
                    person.PA_satisfied_s12 = true;
                    if(bubble_size_now == bubble_size[0]){ //small bubble
                        d_s = person.day_bubble_small;
                        d_t = 17;
                        person.set_day_spot(17, person.day_bubble_small);
                    }else if(bubble_size_now == bubble_size[1]){ //medium bubble
                        d_s = person.day_bubble_medium;
                        d_t = 18;
                        person.set_day_spot(18, person.day_bubble_medium);
                    }else if(bubble_size_now == bubble_size[2]){ //large bubble
                        d_s = person.day_bubble_large;
                        d_t = 19;
                        person.set_day_spot(19, person.day_bubble_large);
                    }
                }else{
                    double r = Math.random();
                    r = Math.round(r*1000)/1000.0;
                    if(r < PA_level){
                        person.PA_satisfied_s12 = true;
                        if(bubble_size_now == bubble_size[0]){ //small bubble
                            d_s = person.day_bubble_small;
                            d_t = 17;
                            person.set_day_spot(17, person.day_bubble_small);
                        }else if(bubble_size_now == bubble_size[1]){ //medium bubble
                            d_s = person.day_bubble_medium;
                            d_t = 18;
                            person.set_day_spot(18, person.day_bubble_medium);
                        }else if(bubble_size_now == bubble_size[2]){ //large bubble
                            d_s = person.day_bubble_large;
                            d_t = 19;
                            person.set_day_spot(19, person.day_bubble_large);
                        }
                    }
                }
            }

            // Scenario 3
            if(person.status <= 2 && is_scenario_3 == true){
                if(PA_level == 1.0){
                    person.PA_satisfied_s3 = true;
                }
                else{
                    double r = Math.random();
                    r = Math.round(r*1000)/1000.0;
                    if(r < PA_level){
                        person.PA_satisfied_s3 = true;
                    }
                }
            }

            // Scenario 5
            if(person.status <= 2 && is_scenario_5 == true){
                if(PA_level == 1.0){
                    person.PA_satisfied_s5 = true;
                }
                else{
                    double r = Math.random();
                    r = Math.round(r*1000)/1000.0;
                    if(r < PA_level){
                        person.PA_satisfied_s5 = true;
                    }
                }
            }

            switch(d_t){
                case 2:
                    s = c.play_group.get(d_s); s.add_agent(person); break;
                case 3:
                    s = c.daycare.get(d_s); s.add_agent(person); break;
                case 4:
                    s = c.elementary.get(d_s); s.add_agent(person); break;
                case 5:
                    s = c.middle.get(d_s); s.add_agent(person); break;
                case 6:
                    s = c.high.get(d_s); s.add_agent(person); break;
                case 7:
                    s = c.workgroup.get(d_s); s.add_agent(person); break;
                case 0:
                    s_daycluster = c.h_cluster.get(person.night_cluster);
                    s_daycluster.add_agent(person);
                    s = c.h_cluster.get(person.night_cluster).homes.get(person.night_spot);
                    s.add_agent(person);
                    break;
                case 1: //처음에 생성할 때 낮에 노는 사람들은 day_type이 0임
                case 8:
                case 9:
                    System.out.println("move_day() doesn't allow neighborhood, and community\t"+d_s); break;
                case 10: // Bed
                    s_daycluster = c.h_cluster.get(person.night_cluster);
                    s_daycluster.add_agent(person);
                    s = c.h_cluster.get(person.night_cluster).homes.get(person.night_spot);
                    s.add_agent(person);
                    break;
                case 11: // Hospital]break;
                case 12: // Treatment center
                    s_daycluster = c.h_cluster.get(person.night_cluster);
                    s_daycluster.add_agent(person);
                    s = c.h_cluster.get(person.night_cluster).homes.get(person.night_spot);
                    s.add_agent(person);
                    break;    
                case 13: // Treatment center cluster
                    break;
                case 14: // Publicly used facility
                    s = c.pub_facilities.get(d_s); s.add_agent(person);
                    break;
                case 15: // General hospital
                    s_daycluster = c.h_cluster.get(person.night_cluster);
                    s_daycluster.add_agent(person);
                    // System.out.println(person.night_spot);
                    s = c.h_cluster.get(person.night_cluster).homes.get(person.night_spot);
                    s.add_agent(person);
                    break;
                case 16: // General hospital cluster
                case 17: // Bubble_small
                    //System.out.println("bubble!!!");
                    s = c.b_cluster_small.get(d_s); s.add_agent(person);
                    break;
                case 18: // Bubble_medium
                    //System.out.println("bubble!!!");
                    s = c.b_cluster_medium.get(d_s); s.add_agent(person);
                    break;
                case 19: // Bubble_large
                    //System.out.println("bubble!!!");
                    s = c.b_cluster_large.get(d_s); s.add_agent(person);
                    break;
                default: System.out.println("There is a type eror: move_day() in Main class");
            }
        }
        // calculte disease propagation in each location and update each agent disease status

        location_update(0, scenario_num_now, bubble_size_now, accepted_range_now); //개인별 status update
    }
    
    // assign a people in each track, community, neighborhood, spot
    // according to his/her predefined home(night) schedule
    private void move_night(){
        //System.out.println("move_Night");
        for(int i = 0; i < numOfppl; i++){
            Agent person = people.get(i);
            Community c = dj.get(0).community.get(person.night_com);
            c.add_agent(person);
            Neighborhood n = c.neighbor.get(person.night_neighbor);
            n.add_agent(person);
            Spot cluster = c.h_cluster.get(person.night_cluster);
            cluster.add_agent(person);
            if(cluster.homes.size() <= person.night_spot){
                person.night_spot =0;
            }
            Spot home = cluster.homes.get(person.night_spot);
            home.add_agent(person);

            if(person.day_type == 17 || person.day_type == 18 || person.day_type == 19){
                person.set_day_spot(person.og_day_type, person.og_day_spot);
            }
        }
        // calculte disease propagation in each location and update each agent disease status
        location_update(1, scenario_num_now, bubble_size_now, accepted_range_now); //개인별 status update
    }


    // calculte disease propagation in each location and update each agent disease status
    private void location_update(int day_type, int scenario_num_now, int bubble_size_now, double accepted_range_now){
        dj.get(0).disease_update(day_type, scenario_num_now, bubble_size_now, accepted_range_now); 
        for(int i = 0; i < numOfppl; i++){
            Agent person = people.get(i);
            person.update_status();
            // 감염자 효과 (집단감염 및 총감염자 합산)
            if(person.infected_today){
                // 다중이용시설 이용으로 집단감염 발생
                if(person.day_type == 14){       
                    Community c = dj.get(0).community.get(person.day_com);
                    if(person.day_spot < c.Assigned_Genhospitals){
                        c.numOfGenbed = Math.max(c.numOfGenbed - 1000,0);  // General hospital shut down
                    }
                }
                // 총감염자 합산
                numOfNewInfection++;
                person.infected_today=false;
            }

            if(person.identified_today==true){
                numOfNewReported++;
                person.identified_today=false;
            }

            if(person.severe_today==true && person.status==5){
                numOfNewSevere++;
                person.severe_today=false;
            }

            if(person.died_today==true){
                numOfNewDead++;
                person.died_today=false;
            }

            if(person.recovered_today==true){
                numOfNewRecovered++;
                person.recovered_today=false;
            }

            if(day_type==1){
                status_count[person.status]++;
                if(person.infection_location<20){
                    infection_location_count[person.infection_location]++;
                    person.infection_location = 999;
                }
                if(person.contact_location<20){
                    contact_location_count[person.contact_location]++;
                    person.contact_location = 999;
                }
            }

            if(day_type == 1 && person.status <= 2 && bubble_size_now > 0){
                if(person.PA_satisfied_s12 == true){
                    daily_PA_satisfied ++;
                    person.PA_satisfied_s12 = false;
                }else{
                    daily_PA_unsatisfied ++;
                }
            }

            if(day_type == 1 && person.status <= 2 && is_scenario_3 == true){
                if(person.PA_satisfied_s3 == true){
                    daily_PA_satisfied ++;
                    person.PA_satisfied_s3 = false;
                }else{
                    daily_PA_unsatisfied ++;
                }
            }

            if(day_type == 1 && person.status <= 2 && is_scenario_5 == true){
                if(person.PA_satisfied_s5 == true){
                    daily_PA_satisfied ++;
                    person.PA_satisfied_s5 = false;
                }else{
                    daily_PA_unsatisfied ++;
                }
            }
       }
    }

    private double calculate_policy_adherence(int scenario_num, int bubble_size_val, double accepted_range_val, double policy_start_time){
        double calculated_PA, slope, average, change, adjusted;
        slope = 0;
        average = 0;
        for(int i = 0; i < weekly_infection.size() - 1; i++){
            change = weekly_infection.get(i+1) - weekly_infection.get(i);
            if(weekly_infection.get(i) == 0){
                average += 0;
            }else{
                average += change / weekly_infection.get(i);
            }
        }
        average /= (weekly_infection.size() - 1);
        adjusted = average * 0.01;

        switch(scenario_num){
            case 1:
                slope = - 0.016;
                break;                
            case 2:
                if(bubble_size_val == 1){
                    slope = - 0.016;
                }else if(bubble_size_val == 2){
                    slope = - 0.008;
                }else if(bubble_size_val == 3){
                    slope = - 0.004;
                }else if(bubble_size_val == 4){
                    slope = - 0.002;
                }else if(bubble_size_val == 5){
                    slope = - 0.001;
                }else{
                    System.out.println("bubble size error!!");
                }
                break;
            case 3:
                slope = - 0.0005;
                break;
            case 4:
                break;
            case 5:
                if(accepted_range_val == 0.0){
                    slope = - 0.016;
                }else if(accepted_range_val == 0.2){
                    slope = - 0.008;
                }else if(accepted_range_val == 0.4){
                    slope = - 0.004;
                }else if(accepted_range_val == 0.6){
                    slope = - 0.002;
                }else if(accepted_range_val == 0.8){
                    slope = - 0.001;
                }else if(accepted_range_val == 0.5){
                    slope = - 0.00025;
                }else{
                    System.out.println("accepted range error!!");
                }break;

        }
        if(scenario_num == 4 || (scenario_num == 5 && accepted_range_val == 1.0)){
            calculated_PA = 0.0;
        }else{
            if(time == 0.0){
                calculated_PA = previous_PA;
            // }else if(time == start_time){
            //     calculated_PA = 0.0;
            }else{
                calculated_PA = previous_PA + slope + adjusted;
            }
        }

        if(calculated_PA > 1.0){
            calculated_PA = 1.0;
        // }else if(calculated_PA < 0.0){
        //     calculated_PA = 0.0;
        }else if(calculated_PA <= 0.3){
            calculated_PA = 0.3;
        }
        System.out.println("slope: " + slope + " prev_PA: " + previous_PA);
        return calculated_PA;
    }

    // private double calculate_policy_adherence(int policy_stringency, double policy_start_time){
        // double starting_PA, current_PA, slope, average, change, adjusted;
        // //starting_PA = 0.5 - 0.0025 * time; //start from 0.5 and end at 0 after 200 days
        // // starting_PA = 1 - 0.001 * time; //start from 1 and end at 0.5 after 500 days
        // starting_PA = 1 - 0.002 * time; //start from 1 and end at 0 after 500 days
        // if(policy_stringency == 1){ //low
        //     // slope = - 0.002;
        //     slope = - 0.004;
        // }else if(policy_stringency == 2){ //medium
        //     // slope = - 0.004;
        //     slope = - 0.008;
        // }else if(policy_stringency == 3){ //high
        //     // slope = - 0.008;
        //     slope = - 0.016;
        // }else{ //default
        //     // slope = - 0.001;
        //     slope = - 0.002;
        // }
        
        // average = 0;
        // for(int i = 0; i < weekly_infection.size() - 1; i++){
        //     change = weekly_infection.get(i+1) - weekly_infection.get(i);
        //     if(weekly_infection.get(i) == 0){
        //         average += 0;
        //     }else{
        //         average += change / weekly_infection.get(i);
        //     }
        // }
        // average /= (weekly_infection.size() - 1);
        // adjusted = average * 0.01;

        // if(policy_stringency == 0){
        //     if(is_scenario_3 == true || is_scenario_5 == true){
        //         current_PA = starting_PA  + adjusted; //Scenario 3, 5
        //     }else{
        //         current_PA = 0.0; //Scenario 1 & 2 & 4
        //     }
        // }else{
        //     current_PA = starting_PA + slope * (time - policy_start_time) + adjusted;
        // }
        // System.out.println("slope: " + slope + " average: " + average);
        // if(current_PA > 1.0){
        //     current_PA = 1.0;
        // }else if(current_PA < 0.0){
        //     current_PA = 0.0;
        // }
        // return current_PA;
    // }

    private int stringency_to_bubble_size(int policy_stringency){
        int bubble_size_ = 0;
        if(policy_stringency == 1){ //low = large bubble
            bubble_size_ = bubble_size[2];
        }else if(policy_stringency == 2){ //medium = medium bubble
            bubble_size_ = bubble_size[1];
        }else if(policy_stringency == 3){ //high = small bubble
            bubble_size_ = bubble_size[0];
        }else{ //default
            bubble_size_ = 0;
        }
        return bubble_size_;
    }
        

    // read file(city information, agent information-schedule)
    private void readFile(String cityfile) throws IOException{
         FileInputStream reader = null;
                try {
                    reader = new FileInputStream(cityfile);
                    StreamTokenizer tokens = new StreamTokenizer(reader);
                    //tokens.quoteChar('\t');
                    int fam_num[];
                    fam_num = new int[numOfCommunity];
                    int c_num=0; int b_num_small = 0; int b_num_medium = 0; int b_num_large = 0;
                    int prev_fam=Integer.MAX_VALUE;
                    int daytype = 0;
                    int family_id, home_neigh, home_comm, day_neigh, day_comm, age_type;
                    Spot cluster, home; cluster = new Spot(1); home = new Spot(0);
                    Spot bubble_small, bubble_medium, bubble_large; bubble_small = new Spot(17); bubble_medium = new Spot(18); bubble_large = new Spot(19);
                    int fam_in_bubble_small = 0; int fam_in_bubble_medium = 0; int fam_in_bubble_large = 0;
                    while (tokens.nextToken() != tokens.TT_EOF) {
                        Agent person = new Agent();
                        person.agent_id = (int)tokens.nval; tokens.nextToken(); 
                        age_type = (int)tokens.nval; tokens.nextToken();
                        family_id = (int)tokens.nval; tokens.nextToken();
                        home_comm = (int)tokens.nval; tokens.nextToken();
                        home_neigh = (int)tokens.nval;tokens.nextToken();
                        day_comm = (int)tokens.nval;  tokens.nextToken();  
                        day_neigh = (int)tokens.nval; tokens.nextToken();
                        daytype = (int)tokens.nval; 
                        
                        person.age_type = age_type;                        
                        person.set_night_community(home_comm);        
                        person.set_home_neighbor(home_neigh); 
                        person.set_day_community(day_comm);
                        person.set_day_neighbor(day_neigh);

                        Community com = dj.get(0).community.get(home_comm); // Track is only one
                        com.add_agent(person);
                        c_num = com.numOfCluster - 1;
                        if(c_num == -1){
                            cluster = new Spot(1);
                            com.add_spot(cluster, 1);
                            c_num = 0;
                        }
                        cluster = com.h_cluster.get(c_num);
                        if(prev_fam != family_id){            
                            home = new Spot(0); 
                            home.spot_id = fam_num[home_comm];
                            person.set_home(fam_num[home_comm]);
                            fam_num[home_comm]++;
                            cluster.add_home(home);
                            person.set_cluster(c_num);
                            if(fam_num[home_comm] > 3){
                                cluster = new Spot(1);
                                com.add_spot(cluster, 1);
                                fam_num[home_comm] = 0;
                            }
                        }else{
                            c_num = com.numOfCluster - 1;
                            person.set_home(fam_num[home_comm]);
                            person.set_cluster(c_num);
                        }
                        // Small bubble
                        b_num_small = com.numOfBubble_small - 1;
                        if(b_num_small == -1){
                            bubble_small = new Spot(17);
                            com.add_spot(bubble_small, 17);
                            b_num_small = 0;
                        }
                        // System.out.println(com.b_cluster_small);
                        bubble_small = com.b_cluster_small.get(b_num_small);
                        if(prev_fam != family_id){  
                            fam_in_bubble_small++;
                            bubble_small.add_home_in_bubble(home, 0);
                            person.set_bubble_small(b_num_small);
                            if(fam_in_bubble_small > bubble_size[0] - 1){
                                bubble_small = new Spot(17);
                                com.add_spot(bubble_small, 17);
                                fam_in_bubble_small = 0;
                            }
                        }else{
                            b_num_small = com.numOfBubble_small - 1;
                            person.set_bubble_small(b_num_small);
                        }
                        if(bubble_size.length > 1){
                            // Medium bubble
                            b_num_medium = com.numOfBubble_medium - 1;
                            if(b_num_medium == -1){
                                bubble_medium = new Spot(18);
                                com.add_spot(bubble_medium, 18);
                                b_num_medium = 0;
                            }
                            //System.out.println(com.b_cluster);
                            bubble_medium = com.b_cluster_medium.get(b_num_medium);
                            if(prev_fam != family_id){  
                                fam_in_bubble_medium++;
                                bubble_medium.add_home_in_bubble(home, 1);
                                person.set_bubble_medium(b_num_medium);
                                if(fam_in_bubble_medium > bubble_size[1] - 1){
                                    bubble_medium = new Spot(18);
                                    com.add_spot(bubble_medium, 18);
                                    fam_in_bubble_medium = 0;
                                }
                            }else{
                                b_num_medium = com.numOfBubble_medium - 1;
                                person.set_bubble_medium(b_num_medium);
                            }
                        }
                        if(bubble_size.length > 2){
                            // Large bubble
                            b_num_large = com.numOfBubble_large - 1;
                            if(b_num_large == -1){
                                bubble_large = new Spot(19);
                                com.add_spot(bubble_large, 19);
                                b_num_large = 0;
                            }
                            //System.out.println(com.b_cluster);
                            bubble_large = com.b_cluster_large.get(b_num_large);
                            if(prev_fam != family_id){  
                                fam_in_bubble_large++;
                                bubble_large.add_home_in_bubble(home, 2);
                                person.set_bubble_large(b_num_large);
                                if(fam_in_bubble_large > bubble_size[2] - 1){
                                    bubble_large = new Spot(19);
                                    com.add_spot(bubble_large, 19);
                                    fam_in_bubble_large = 0;
                                }
                            }else{
                                b_num_large = com.numOfBubble_large - 1;
                                person.set_bubble_large(b_num_large);
                            }
                        }

                        prev_fam = family_id;

                        com = dj.get(0).community.get(day_comm);
                        switch(daytype){
                            case 2: // preschool
                                if(com.play_group.get(com.play_group.size()-1).add_agent(person) == false){
                                    Spot h = new Spot(2);
                                    com.add_spot(h, 2);
                                }
                                person.set_day_spot(2, (com.play_group.size()-1));
                                person.set_og_day_type(2, (com.play_group.size()-1));
                                break;
                            case 3: 
                               if(com.daycare.get(com.daycare.size() - 1).add_agent(person) ==  false){
                                    Spot h = new Spot(3);
                                    com.add_spot(h, 3);
                                }
                                person.set_day_spot(3, (com.daycare.size() - 1));
                                person.set_og_day_type(3, (com.daycare.size() - 1));
                                break;
                            case 4:
                                if(com.elementary.get(com.elementary.size()-1).add_agent(person) == false){
                                    Spot h = new Spot(4);
                                    com.add_spot(h, 4);
                                }
                                person.set_day_spot(4, com.elementary.size()-1);
                                person.set_og_day_type(4, com.elementary.size()-1);
                                break;
                            case 5:
                                if(com.middle.get(com.middle.size()-1).add_agent(person) == false){
                                    Spot h = new Spot(5);
                                    com.add_spot(h, 5);
                                }
                                person.set_day_spot(5, com.middle.size()-1);
                                person.set_og_day_type(5, com.middle.size()-1);
                                break;
                            case 6:
                                if(com.high.get(com.high.size()-1).add_agent(person) == false){
                                    Spot h = new Spot(6);
                                    com.add_spot(h, 6);
                                }
                                person.set_day_spot(6, com.high.size()-1); 
                                person.set_og_day_type(6, com.high.size()-1);
                                break;
                            case 7:
                                if(com.workgroup.get(com.workgroup.size()-1).add_agent(person) == false){
                                    Spot h = new Spot(7);
                                    com.add_spot(h, 7);
                                }
                                person.set_day_spot(7, com.workgroup.size()-1);
                                person.set_og_day_type(7, com.workgroup.size()-1);
                                break;
                            case 0:
                                person.set_day_spot(0, person.night_spot);
                                person.set_og_day_type(0, person.night_spot);
                                person.set_day_neighbor(person.night_neighbor);
                                person.set_day_community(person.night_com);
                                break;
                            default:
                                System.out.println("Out of bound: not a valid day spot");
                                break;
                        }
                        people.add(person);
                    }
                    census_max_family_id = prev_fam;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    }
    
    private ArrayList<Agent> cloning(int total_pop){
        int num_clone = people.size();
        int census_size = people.size();
        ArrayList<Agent> people_new = people;
        int fam_num[];
        fam_num = new int[numOfCommunity];
        int one_cycle = 0;
        int cycle_count = 0;
        int prev_fam=Integer.MAX_VALUE;
        int fam_in_bubble_small = 0; int fam_in_bubble_medium = 0; int fam_in_bubble_large = 0;
        
        while(num_clone < total_pop){
            if(one_cycle == census_size){
                one_cycle = 0;
                cycle_count++;
            }
            
            int c_num=0; int b_num_small = 0; int b_num_medium = 0; int b_num_large = 0;
            int daytype = 0;
            int family_id, home_neigh, home_comm, day_neigh, day_comm, age_type;
            Spot cluster, home; cluster = new Spot(1); home = new Spot(0); 
            Spot bubble_small, bubble_medium, bubble_large; bubble_small = new Spot(17); bubble_medium = new Spot(18); bubble_large = new Spot(19);
            Agent person = new Agent();
            
            person.agent_id = num_clone; 
            age_type = people.get(one_cycle).age_type;
            family_id = (1+cycle_count)*census_max_family_id + people.get(one_cycle).night_cluster * 4 + people.get(one_cycle).night_spot + 1;
            home_comm = people.get(one_cycle).night_com;
            home_neigh = people.get(one_cycle).night_neighbor;
            day_comm = people.get(one_cycle).day_com;  
            day_neigh = people.get(one_cycle).day_neighbor;
            daytype = people.get(one_cycle).day_type; 

            person.age_type = age_type;                        
            person.set_night_community(home_comm);        
            person.set_home_neighbor(home_neigh); 
            person.set_day_community(day_comm);
            person.set_day_neighbor(day_neigh);

            Community com = dj.get(0).community.get(home_comm); // Track is only one
            com.add_agent(person);
            c_num = com.numOfCluster - 1;
            
            cluster = com.h_cluster.get(c_num);
            if(prev_fam != family_id){
                home = new Spot(0); 
                home.spot_id = fam_num[home_comm];
                person.set_home(fam_num[home_comm]);
                fam_num[home_comm]++;
                cluster.add_home(home);
                person.set_cluster(c_num);
                if(fam_num[home_comm] > 3){
                    cluster = new Spot(1);
                    com.add_spot(cluster, 1);
                    fam_num[home_comm] = 0;
                }
            }else{
                c_num = com.numOfCluster - 1;
                person.set_home(fam_num[home_comm]);
                person.set_cluster(c_num);
            }

            // Small bubble
            b_num_small = com.numOfBubble_small - 1;

            if(b_num_small == 0){
                bubble_small = new Spot(17);
                com.add_spot(bubble_small, 17);
                b_num_small = 1;
            }

            bubble_small = com.b_cluster_small.get(b_num_small);
            if(prev_fam != family_id){  
                fam_in_bubble_small++;
                bubble_small.add_home_in_bubble(home, 0);
                person.set_bubble_small(b_num_small);
                if(fam_in_bubble_small > bubble_size[0] - 1){
                    bubble_small = new Spot(17);
                    com.add_spot(bubble_small, 17);
                    fam_in_bubble_small = 0;
                }
            }else{
                b_num_small = com.numOfBubble_small - 1;
                person.set_bubble_small(b_num_small);
            }
            if(bubble_size.length > 1){
                // Medium bubble
                b_num_medium = com.numOfBubble_medium - 1;
                if(b_num_medium == 0){
                    bubble_medium = new Spot(18);
                    com.add_spot(bubble_medium, 18);
                    b_num_medium = 1;
                }
                //System.out.println(com.b_cluster);
                bubble_medium = com.b_cluster_medium.get(b_num_medium);
                if(prev_fam != family_id){  
                    fam_in_bubble_medium++;
                    bubble_medium.add_home_in_bubble(home, 1);
                    person.set_bubble_medium(b_num_medium);
                    if(fam_in_bubble_medium > bubble_size[1] - 1){
                        bubble_medium = new Spot(18);
                        com.add_spot(bubble_medium, 18);
                        fam_in_bubble_medium = 0;
                    }
                }else{
                    b_num_medium = com.numOfBubble_medium - 1;
                    person.set_bubble_medium(b_num_medium);
                }
            }
            if(bubble_size.length > 2){
                // Large bubble
                b_num_large = com.numOfBubble_large - 1;
                if(b_num_large == 0){
                    bubble_large = new Spot(19);
                    com.add_spot(bubble_large, 19);
                    b_num_large = 1;
                }
                //System.out.println(com.b_cluster);
                bubble_large = com.b_cluster_large.get(b_num_large);
                if(prev_fam != family_id){  
                    fam_in_bubble_large++;
                    bubble_large.add_home_in_bubble(home, 2);
                    person.set_bubble_large(b_num_large);
                    if(fam_in_bubble_large > bubble_size[2] - 1){
                        bubble_large = new Spot(19);
                        com.add_spot(bubble_large, 19);
                        fam_in_bubble_large = 0;
                    }
                }else{
                    b_num_large = com.numOfBubble_large - 1;
                    person.set_bubble_large(b_num_large);
                }
            }

            prev_fam = family_id;
            com = dj.get(0).community.get(day_comm); // change to day community
            switch(daytype){
                case 2: // preschool
                    if(com.play_group.get(com.play_group.size()-1).add_agent(person) == false){
                        Spot h = new Spot(2);
                        com.add_spot(h, 2);
                    }
                    person.set_day_spot(2, (com.play_group.size()-1));
                    person.set_og_day_type(2, (com.play_group.size()-1));
                    break;
                case 3: 
                   if(com.daycare.get(com.daycare.size() - 1).add_agent(person) ==  false){
                        Spot h = new Spot(3);
                        com.add_spot(h, 3);
                    }
                    person.set_day_spot(3, (com.daycare.size() - 1));
                    person.set_og_day_type(3, (com.daycare.size() - 1));
                    break;
                case 4:
                    if(com.elementary.get(com.elementary.size()-1).add_agent(person) == false){
                        Spot h = new Spot(4);
                        com.add_spot(h, 4);
                    }
                    person.set_day_spot(4, com.elementary.size()-1);
                    person.set_og_day_type(4, com.elementary.size()-1);
                    break;
                case 5:
                    if(com.middle.get(com.middle.size()-1).add_agent(person) == false){
                        Spot h = new Spot(5);
                        com.add_spot(h, 5);
                    }
                    person.set_day_spot(5, com.middle.size()-1);
                    person.set_og_day_type(5, com.middle.size()-1);
                    break;
                case 6:
                    if(com.high.get(com.high.size()-1).add_agent(person) == false){
                        Spot h = new Spot(6);
                        com.add_spot(h, 6);
                    }
                    person.set_day_spot(6, com.high.size()-1); 
                    person.set_og_day_type(6, com.high.size()-1);
                    break;
                case 7:
                    if(com.workgroup.get(com.workgroup.size()-1).add_agent(person) == false){
                        Spot h = new Spot(7);
                        com.add_spot(h, 7);
                    }
                    person.set_day_spot(7, com.workgroup.size()-1);
                    person.set_og_day_type(7, com.workgroup.size()-1);
                    break;
                case 0:
                    person.set_day_spot(0, person.night_spot);
                    person.set_og_day_type(0, person.night_spot);
                    person.set_day_neighbor(person.night_neighbor);
                    person.set_day_cluster(person.night_cluster);
                    person.set_day_community(person.night_com);
                    break;
                default:
                    System.out.println("Out of bound: not a valid day spot");
                    break;
            }
            people_new.add(person);
            num_clone++;
            one_cycle++;
        }    

        // 병원, Tcenter, Genbed 생성
        for(int c = 0; c <  dj.get(0).numOfCommunity; c++){
            Community com = dj.get(0).community.get(c);
            int k = 0;
            while(k < com.Assigned_ICU_Beds){
                Spot hospital = new Spot(1);
                for(int bed = 0; bed < Math.min(25,(com.Assigned_ICU_Beds - com.numOfICUBed)); bed++){  // 병원 당 25개 병상, 수정 시 Community - add_spot의 numOfBed+25; 도 수정 필요
                    Spot bed_new = new Spot(10);
                    bed_new.spot_id = bed;
                    hospital.add_home(bed_new);
                    k++;
                }
                com.add_spot(hospital, 11);
            }
            // Treatment construction          
            k = 0;
            Spot Treatment_center_cluster = new Spot(1);
            while(k < com.Assigned_Tcenters){
                Spot Tcenter_new = new Spot(12);
                Treatment_center_cluster.add_home(Tcenter_new);
                k++;
                com.add_spot(Treatment_center_cluster, 13);   // 만들어 줄때마다 넣어야함
            }
            // General hospital construction         
            k = 0;
            Spot General_hospital_cluster = new Spot(1);
            while(k < com.Assigned_Genhospitals){
                Spot Genhospital_new = new Spot(15); 
                General_hospital_cluster.add_home(Genhospital_new); // inpatient
                Spot pub_facility = new Spot(14);
                com.add_spot(pub_facility, 14); // outpatient
                k++;
                com.add_spot(General_hospital_cluster, 16);   // 만들어 줄때마다 넣어야함
            }
            // Publicly used facility construction
            Spot pub_facility = new Spot(14);
            com.add_spot(pub_facility, 14); // Another Public facility, if com.pub_facility.size() == n, then n-1 outpatient hospital + 1 public facility
            
            double a = com.daycare.size();
            double b = com.play_group.size();
            double cc = com.elementary.size();
            double d = com.middle.size();
            double e = com.high.size();
            double f = com.workgroup.size();
            System.out.println("Community "+ c +" is constructed with: "+ com.ppl.size()+", daycare: " + a + ", play_group:  " + b + ", elementary: " + cc + ", middle: " + d 
                    + ", high: " + e + ", workgroup: " + f + ", hospital beds: " + com.numOfICUBed + ", Tcenter rooms: " + com.numOfTCRoom + ", general hospital beds: " + com.numOfGenbed 
                    + ", small bubbles: " + com.numOfBubble_small + ", medium bubbles: " + com.numOfBubble_medium + ", large bubbles: " + com.numOfBubble_large + ", clusters: " + com.numOfCluster);
        } 
        return(people_new);
    }
    
    private void generate_step_1(){
        for(int i = 0; i < numOfTrack; i++){
            Track new_track = new Track();
            dj.add(new_track);
            for(int j = 0; j < numOfCommunity; j++){
                Community new_com = new Community((int)numOfppl/(numOfTrack*numOfCommunity));
                new_com.community_id = j;
                dj.get(i).add_community(new_com);
                for(int k = 2; k < 8; k++){         // playgroup에서 workgroup까지만 spot 생성
                    Spot h = new Spot(k);
                    h.spot_id = 0;                  // spot의 id는 initially 0으로 setting(-)
                    new_com.add_spot(h, k);
                }
                for(int m = 0; m < 4; m++){
                    Neighborhood new_neigh = new Neighborhood();
                    new_com.add_neighborhood(new_neigh);
                }
            }
        }
    }

    private void generate_step_2() throws IOException{
        String s = System.getProperty("user.dir");
        readFile(s+filename);
        ArrayList<Agent> people_new = cloning(numOfppl);
        people = people_new;
    }

    public double getTime(){
        return this.time;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        Main m = new Main();
        double[][] new_infection, new_reported, new_quarantine, new_recovered, new_severe, new_dead; 
        double[][] total_infection, total_severe, total_recovered, total_dead, total_reported, total_quarantine;
        double[][] Genbed_using, ICU_using, HR_using;
        double[][][] total_status_count, SIR_count, infection_location_count, contact_location_count;
        double[][] final_PA_level, final_PA_satisfied, final_PA_unsatisfied;

        total_status_count = new double[run+1][runtime][11];
        infection_location_count = new double[run+1][runtime][23];
        contact_location_count = new double[run+1][runtime][23];
        SIR_count = new double[run+1][runtime][5];

        new_infection = new double[run+1][runtime];
        new_reported = new double[run+1][runtime];
        new_severe = new double[run+1][runtime];
        new_dead = new double[run+1][runtime];
        new_recovered = new double[run+1][runtime];
        new_quarantine = new double[run+1][runtime];

        total_infection = new double[run+1][runtime];
        total_reported = new double[run+1][runtime];
        total_severe = new double[run+1][runtime];
        total_dead = new double[run+1][runtime];
        total_recovered = new double[run+1][runtime];
        total_quarantine = new double[run+1][runtime];
        
        Genbed_using = new double[run+1][runtime];
        ICU_using = new double[run+1][runtime];
        HR_using = new double[run+1][runtime];
        final_PA_level = new double[run+1][runtime];
        final_PA_satisfied = new double[run+1][runtime];
        final_PA_unsatisfied = new double[run+1][runtime];

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
        int valid_exp = 0;
        int no_pandemic = 0;

        //replication
        for(int i = 1; i < run+1; i++){
            m.initialize(numOfppl_);
            System.out.println("iteration " + i +" is initialized.");
            timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(timestamp);
                    
            int t_new_infection = 0;
            int t_dead = 0;
            int t_severe = 0;
            int t_reported = 0;
            while(m.time < runtime){
                System.out.println("iteration "+i+", day "+m.time);
                m.disease_update();

                new_infection[i][(int)m.time] = m.numOfNewInfection;
                new_reported[i][(int)m.time] = m.numOfNewReported;
                new_severe[i][(int)m.time] = m.numOfNewSevere;
                new_dead[i][(int)m.time] = m.numOfNewDead;
                new_recovered[i][(int)m.time] = m.numOfNewRecovered;
                new_quarantine[i][(int)m.time] = m.numOfNewQuarantine;

                total_infection[i][(int)m.time] = m.numOfInfection;
                total_severe[i][(int)m.time] = m.numOfSevere;
                total_recovered[i][(int)m.time] = m.numOfRecovered;
                total_dead[i][(int)m.time] = m.numOfDead;
                total_reported[i][(int)m.time] = m.numOfReported;
                total_quarantine[i][(int)m.time] = m.numOfQuarantine;
                final_PA_level[i][(int)m.time] = m.PA_level;
                final_PA_satisfied[i][(int)m.time] = m.daily_PA_satisfied;
                final_PA_unsatisfied[i][(int)m.time] = m.daily_PA_unsatisfied;

                Spot tempGenbed = new Spot(15);
                Genbed_using[i][(int)m.time] = tempGenbed.max_pplNum - m.dj.get(0).community.get(0).numOfGenbed;
                ICU_using[i][(int)m.time] = m.dj.get(0).community.get(0).Assigned_ICU_Beds - m.dj.get(0).community.get(0).numOfICUBed;
                HR_using[i][(int)m.time] = (int)(Genbed_using[i][(int)m.time]/13) + 1 * ICU_using[i][(int)m.time];

                for(int status = 0; status < 11; status++){
                    total_status_count[i][(int)m.time][status] = m.status_count[status];
                    m.status_count[status]=0;
                }

                for(int type = 0; type < 20; type++){
                    infection_location_count[i][(int)m.time][type] = m.infection_location_count[type];
                    m.infection_location_count[type]=0;
                }

                for(int type = 0; type < 20; type++){
                    contact_location_count[i][(int)m.time][type] = m.contact_location_count[type];
                    m.contact_location_count[type]=0;
                }

                // S
                SIR_count[i][(int)m.time][0] = total_status_count[i][(int)m.time][0];
                // I
                SIR_count[i][(int)m.time][1] = total_status_count[i][(int)m.time][1] + total_status_count[i][(int)m.time][2] + total_status_count[i][(int)m.time][3] + total_status_count[i][(int)m.time][4] + total_status_count[i][(int)m.time][5] + total_status_count[i][(int)m.time][6];
                // R
                SIR_count[i][(int)m.time][2] = total_status_count[i][(int)m.time][7] + total_status_count[i][(int)m.time][8];
                // Q (Quarantined)
                SIR_count[i][(int)m.time][3] = total_status_count[i][(int)m.time][9];
                // V (Vaccinated)
                SIR_count[i][(int)m.time][4] = total_status_count[i][(int)m.time][10];

                m.time++;
                t_new_infection += m.numOfNewInfection;
            }
            
            if(total_dead[i][(runtime - 1)] > 0){
                m.time = 0.0;
                while(m.time < runtime){
                    new_infection[0][(int)m.time] += new_infection[i][(int)m.time];
                    new_reported[0][(int)m.time] += new_reported[i][(int)m.time];
                    new_severe[0][(int)m.time] += new_severe[i][(int)m.time];
                    new_dead[0][(int)m.time] += new_dead[i][(int)m.time];
                    new_recovered[0][(int)m.time] += new_recovered[i][(int)m.time];
                    new_quarantine[0][(int)m.time] += new_quarantine[i][(int)m.time];

                    total_infection[0][(int)m.time] += total_infection[i][(int)m.time];
                    total_severe[0][(int)m.time] += total_severe[i][(int)m.time];
                    total_dead[0][(int)m.time] += total_dead[i][(int)m.time];
                    total_recovered[0][(int)m.time] += total_recovered[i][(int)m.time];
                    total_reported[0][(int)m.time] += total_reported[i][(int)m.time];
                    total_quarantine[0][(int)m.time] += total_quarantine[i][(int)m.time];

                    Genbed_using[0][(int)m.time] += Genbed_using[i][(int)m.time];
                    ICU_using[0][(int)m.time] += ICU_using[i][(int)m.time];
                    HR_using[0][(int)m.time] += HR_using[i][(int)m.time];
                    final_PA_level[0][(int)m.time] += final_PA_level[i][(int)m.time];
                    final_PA_satisfied[0][(int)m.time] += final_PA_satisfied[i][(int)m.time];
                    final_PA_unsatisfied[0][(int)m.time] += final_PA_unsatisfied[i][(int)m.time];

                    for(int status = 0; status < 11; status++){
                        total_status_count[0][(int)m.time][status] += total_status_count[i][(int)m.time][status];
                    }
                    for(int type = 0; type < 20; type++){
                        infection_location_count[0][(int)m.time][type] += infection_location_count[i][(int)m.time][type];
                    }
                    for(int type = 0; type < 20; type++){
                        contact_location_count[0][(int)m.time][type] += contact_location_count[i][(int)m.time][type];
                    }
                    for(int k = 0; k < 5; k++){
                        SIR_count[0][(int)m.time][k] += SIR_count[i][(int)m.time][k];
                    }
                    m.time++;
                }
                valid_exp ++;
            }
                
            t_dead = m.numOfDead;
            t_severe = m.numOfSevere; 
            t_reported = m.numOfReported;
            System.out.println("iteration " + i + "--------------------------- Total infected: " + t_new_infection + "---Total Dead: " + t_dead + "---Total Severe: " + t_severe + "---Total Reported: " + t_reported);
            timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(timestamp);

            if(t_new_infection<100){
                no_pandemic ++;
            }
        }

        //spot, neighborhood, community infection 각각 세는 코드
        int numOfinfected_from_spot = 0;
        int numOfinfected_from_neighborhood = 0;
        int numOfinfected_from_community = 0;
        for(int i = 0; i < numOfppl_; i++){
            Agent person = people.get(i);
            if(person.infected_spot==true){
                numOfinfected_from_spot ++;
            }else if(person.infected_neighborhood==true){
                numOfinfected_from_neighborhood ++;
            }else if(person.infected_community==true){
                numOfinfected_from_community ++;
            }
        }
        System.out.println("infected from spot : " + numOfinfected_from_spot);
        System.out.println("infected from neighborhood : " + numOfinfected_from_neighborhood);
        System.out.println("infected from community : " + numOfinfected_from_community);
        System.out.println("num of no pandemic : " + no_pandemic);
        System.out.println("Experiment Finished.");
        timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
        
        //Average file
        String s = System.getProperty("user.dir");
        File f = new File(s);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
        FileWriter writer = new FileWriter(f.getParent()+"/MCM-simulation-main00/output/S"+scenario_num+"_Average_data"+"_Valid"+valid_exp+"_"+simpleDateFormat.format(timestamp).substring(0,19)+".csv");
        writer.append("Time" + ",New infection" + ",Total infection"+ ", New Reported"+ ", Total Reported" + ",New Severe" + ",Total Severe" + ",New Dead" + ",Total Dead"+ ",New Recovered" + ",Total Recovered"+ ",New Quarantined" + ", Total Quarantined");
        writer.append(",,,Status Count");
        for(int status = 0; status < 11; status++){
            writer.append(","+ Integer.toString(status));
        }
        writer.append(",,,Infection Location Count");
        for(int type = 0; type < 20; type++){
            writer.append(","+ Integer.toString(type));
        }
        writer.append(",,,Contact Location Count");
        for(int type = 0; type < 20; type++){
            writer.append(","+ Integer.toString(type));
        }
        writer.append(",,S,I,R,Q,V");
        writer.append(",,Genbed using,ICU using, Human Resource needed");
        writer.append(",,PA level, PA satisfied, PA unsatisfied");
        writer.append('\n');
        for(int i = 0; i < runtime; i++){     
            //n번 평균
            new_infection[0][i] /= (valid_exp+0.0001);
            new_reported[0][i] /= (valid_exp+0.0001);
            new_severe[0][i] /= (valid_exp+0.0001);
            new_dead[0][i] /= (valid_exp+0.0001);
            new_recovered[0][i] /= (valid_exp+0.0001);
            new_quarantine[0][i] /= (valid_exp+0.0001);

            total_infection[0][i] /= (valid_exp+0.0001);
            total_severe[0][i] /= (valid_exp+0.0001);
            total_recovered[0][i] /= (valid_exp+0.0001);
            total_dead[0][i] /= (valid_exp+0.0001);
            total_reported[0][i] /= (valid_exp+0.0001);
            total_quarantine[0][i] /= (valid_exp+0.0001);

            Genbed_using[0][i] /= (valid_exp+0.0001);
            ICU_using[0][i] /= (valid_exp+0.0001);
            HR_using[0][i] /= (valid_exp+0.0001);
            final_PA_level[0][i] /= (valid_exp+0.0001);
            final_PA_satisfied[0][i] /= (valid_exp+0.0001);
            final_PA_unsatisfied[0][i] /= (valid_exp+0.0001);

            for(int status = 0; status < 11; status++){
                total_status_count[0][i][status] /= (valid_exp+0.0001);
            }
            for(int type = 0; type < 20; type++){
                infection_location_count[0][i][type] /= (valid_exp+0.0001);
            }
            for(int type = 0; type < 20; type++){
                contact_location_count[0][i][type] /= (valid_exp+0.0001);
            }
            for(int k = 0; k < 5; k++){
                SIR_count[0][i][k] /= (valid_exp+0.0001);
            }
            writer.append((i+1) + "," + new_infection[0][i] + ","+ total_infection[0][i] + "," + new_reported[0][i] + "," + total_reported[0][i] + "," + new_severe[0][i] + "," + total_severe[0][i] + "," + new_dead[0][i] + "," + total_dead[0][i] + "," + new_recovered[0][i] + "," + total_recovered[0][i] + "," + new_quarantine[0][i] + "," + total_quarantine[0][i]);
            writer.append(",,,"+(i+1));
            for(int status = 0; status < 11; status++){
                writer.append(","+ total_status_count[0][i][status]);
            }
            writer.append(",,,"+(i+1));
            for(int type = 0; type < 20; type++){
                writer.append(","+ infection_location_count[0][i][type]);
            }
            writer.append(",,,"+(i+1));
            for(int type = 0; type < 20; type++){
                writer.append(","+ contact_location_count[0][i][type]);
            }

            writer.append(",,"+ SIR_count[0][i][0] + ","+ SIR_count[0][i][1] + ","+ SIR_count[0][i][2]+ ","+ SIR_count[0][i][3] + ","+ SIR_count[0][i][4]);
            writer.append(",,"+ Genbed_using[0][i] + "," + ICU_using[0][i] + "," + HR_using[0][i]);
            writer.append(",,"+ final_PA_level[0][i] + "," + final_PA_satisfied[0][i] + "," + final_PA_unsatisfied[0][i]);
            writer.append('\n');
        }
        writer.close();

        //iteration file
        for(int j = 1; j < run + 1; j++){
            String outname = "/MCM-simulation-main00/output/S"+scenario_num+"_Iteration_" + j + "_"+simpleDateFormat.format(timestamp).substring(0,19)+".csv";
            FileWriter writer_temp = new FileWriter(f.getParent()+outname);
            writer_temp.append("Time" + ",New infection" + ",Total infection"+ ", New Reported"+ ", Total Reported" + ",New Severe" + ",Total Severe" + ",New Dead" + ",Total Dead"+ ",New Recovered" + ",Total Recovered"+ ",New Quarantined" + ", Total Quarantined");
            writer_temp.append(",,,Status Count");
            for(int status = 0; status < 11; status++){
                writer_temp.append(","+ Integer.toString(status));
            }
            writer_temp.append(",,,Infection Location Count");
            for(int type = 0; type < 20; type++){
                writer_temp.append(","+ Integer.toString(type));
            }
            writer_temp.append(",,,Contact Location Count");
            for(int type = 0; type < 20; type++){
                writer_temp.append(","+ Integer.toString(type));
            }
            writer_temp.append(",,S,I,R,Q,V");
            writer_temp.append(",,Genbed using,ICU using,Human Resource needed");
            writer_temp.append(",,PA level, PA satisfied, PA unsatisfied");
            writer_temp.append('\n');
            for(int i = 0; i < runtime; i++){     
                writer_temp.append((i+1) + "," + new_infection[j][i] + ","+ total_infection[j][i] + "," + new_reported[j][i] + "," + total_reported[j][i] + "," + new_severe[j][i] + "," + total_severe[j][i] + "," + new_dead[j][i] + "," + total_dead[j][i] + "," + new_recovered[j][i] + "," + total_recovered[j][i] + "," + new_quarantine[j][i] + "," + total_quarantine[j][i]);
                writer_temp.append(",,,"+(i+1));
                for(int status = 0; status < 11; status++){
                    writer_temp.append(","+ total_status_count[j][i][status]);
                }
                writer_temp.append(",,,"+(i+1));
                for(int type = 0; type < 20; type++){
                    writer_temp.append(","+ infection_location_count[j][i][type]);
                }
                writer_temp.append(",,,"+(i+1));
                for(int type = 0; type < 20; type++){
                    writer_temp.append(","+ contact_location_count[j][i][type]);
                }

                writer_temp.append(",,"+ SIR_count[j][i][0] + ","+ SIR_count[j][i][1] + ","+ SIR_count[j][i][2] + ","+ SIR_count[j][i][3] + ","+ SIR_count[j][i][4]);
                writer_temp.append(",,"+ Genbed_using[j][i] + "," + ICU_using[j][i] + "," + HR_using[j][i]);
                writer_temp.append(",,"+ final_PA_level[j][i] + "," + final_PA_satisfied[j][i] + "," + final_PA_unsatisfied[j][i]);
                writer_temp.append('\n');
            }
            writer_temp.close();
        }

        // Numbers file
        for(int j = 1; j < run + 1; j++){
            String outname = "/MCM-simulation-main00/output/S"+scenario_num+"_Numbers_Iteration_" + j + "_"+simpleDateFormat.format(timestamp).substring(0,19)+".csv";
            FileWriter writer_temp = new FileWriter(f.getParent()+outname);
            writer_temp.append("total population," + Integer.toString(numOfppl_)+"\n");

            writer_temp.append("\n");
            writer_temp.append("total Infected," + total_infection[j][runtime-1]+"\n");
            writer_temp.append("ratio to total population," + (total_infection[j][runtime-1]/numOfppl_)+"\n");
            double maxInfection = 0;
            int maxInfectionIndex = 0;
            for(int i = 0; i < runtime; i++){     
                if(new_infection[j][i]>maxInfection){
                    maxInfection = new_infection[j][i];
                    maxInfectionIndex = i;}}
            writer_temp.append("max Infection," + maxInfection+"\n");
            writer_temp.append("max Infection day," + (maxInfectionIndex+1)+"\n");

            writer_temp.append("\n");
            writer_temp.append("total Reported," + total_reported[j][runtime-1]+"\n");
            writer_temp.append("ratio to total population," + (total_reported[j][runtime-1]/numOfppl_)+"\n");
            writer_temp.append("ratio to total infected," + (total_reported[j][runtime-1]/total_infection[j][runtime-1])+"\n");

            writer_temp.append("\n");
            writer_temp.append("total Severe," + total_severe[j][runtime-1]+"\n");
            writer_temp.append("ratio to total population," + (total_severe[j][runtime-1]/numOfppl_)+"\n");
            writer_temp.append("ratio to total infected," + (total_severe[j][runtime-1]/total_infection[j][runtime-1])+"\n");

            writer_temp.append("\n");
            writer_temp.append("total Dead," + total_dead[j][runtime-1]+"\n");
            writer_temp.append("ratio to total population," + (total_dead[j][runtime-1]/numOfppl_)+"\n");
            writer_temp.append("ratio to total infected," + (total_dead[j][runtime-1]/total_infection[j][runtime-1])+"\n");
            writer_temp.append("ratio to total severe," + (total_dead[j][runtime-1]/total_severe[j][runtime-1])+"\n");

            writer_temp.append("\n");
            writer_temp.append("total Recovered," + total_recovered[j][runtime-1]+"\n");
            writer_temp.append("ratio to total population," + (total_recovered[j][runtime-1]/numOfppl_)+"\n");
            writer_temp.append("ratio to total infected," + (total_recovered[j][runtime-1]/total_infection[j][runtime-1])+"\n");

            writer_temp.append("\n");
            writer_temp.append("total Quarantined," + total_quarantine[j][runtime-1]+"\n");
            writer_temp.append("ratio to total population," + (total_quarantine[j][runtime-1]/numOfppl_)+"\n");
            writer_temp.append("ratio to total infected," + (total_quarantine[j][runtime-1]/total_infection[j][runtime-1])+"\n");
            
            writer_temp.append("\n");
            double maxHR = 0;
            int maxHRIndex = 0;
            for(int i = 0; i < runtime; i++){     
                if(HR_using[j][i]>maxHR){
                    maxHR = HR_using[j][i];
                    maxHRIndex = i;}}
            writer_temp.append("max Human Resource needed," + maxHR+"\n");
            writer_temp.append("max Human Resource needed day," + (maxHRIndex+1)+"\n");

            writer_temp.close();
        }

        //GraphData file
        for(int j = 1; j < run + 1; j++){
            String outname = "/MCM-simulation-main00/output/S"+scenario_num+"_GraphData_Iteration_" + j + "_"+simpleDateFormat.format(timestamp).substring(0,19)+".csv";
            FileWriter writer_temp = new FileWriter(f.getParent()+outname);
            writer_temp.append("Time," + ",Daily infection" + ",Daily Reported," + ",Cumulative infection"+ ",Cumulative Reported,");
            writer_temp.append(",Asymptomatic infected" + ",Symptomatic infected" + ",Unsevere Reported" + ",Severe Reported,");
            writer_temp.append(",Daily Severe" + ",Daily Dead," + ",Cumulative Severe" + ",Cumulative Dead,");
            writer_temp.append(",S,I,R,Q,V,");
            writer_temp.append(",Genbed using" + ",ICU using" + ",Human Resource");

            writer_temp.append('\n');
            for(int i = 0; i < runtime; i++){     
                writer_temp.append((i+1) + ",," + new_infection[j][i] + "," + new_reported[j][i] + ",,"+ total_infection[j][i] + "," + total_reported[j][i] + ",");
                writer_temp.append(","+ total_status_count[j][i][1] + ","+ total_status_count[j][i][2] + ","+ total_status_count[j][i][4] + ","+ total_status_count[j][i][6] + ",");
                writer_temp.append(","+ new_severe[j][i] + ","+ new_dead[j][i] + ",,"+ total_severe[j][i] + ","+ total_dead[j][i] + ",");
                writer_temp.append(","+ SIR_count[j][i][0] + ","+ SIR_count[j][i][1] + ","+ SIR_count[j][i][2] + ","+ SIR_count[j][i][3] + ","+ SIR_count[j][i][4] + ",");
                writer_temp.append(","+ Genbed_using[j][i] + "," + ICU_using[j][i] + "," + HR_using[j][i]);
                writer_temp.append('\n');
            }
            writer_temp.close();
        }
    }
}
