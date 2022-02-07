import java.util.Random;

public class Agent {
    int agent_id;
    int age, asymptomatic_period; 
    int status; // 감염관련 분류
    /* 0: susceptible               (미감염)
     * 1: infected, nonsymptomatic  (감염, 무증상, 미확진) 
     * 2: infected, symptomatic     (감염, 증상, 미확진)
     * 3: confirmed                 (감염, 확진, 분류전)
     * 4: non-severe                (감염, 확진, 경증)
     * 5: severe                    (감염, 확진, 위중증)
     * 6: severe in ICU             (감염, 확진, 위중증, ICU)
     * 7: recovered                 (완치)
     * 8: dead                      (사망)
     * 9: quarantined               (자가격리)
     * 10: vaccinated               (백신접종)
     */ 
     
    int bed_type=0;// 확진자 격리장소 분류
    /* 0: 해당사항 없음
     * 1: Withdraw(자가격리)
     * 2: Tcenter(생활치료센터)
     * 3: Genbed(음압병상)
     * 4: ICU(중환자실)
     * 5: Untreated severe(병상없는 중환자)
     */

    int age_type;// 연령대 분류
    /* 0: ~7
     * 1: 8~19
     * 2: 20~29
     * 3: 30~65
     * 4: 65~
    */

    double infectiousness;
    double dt;
    double withdraw_prob;
    double infected_period;
    double spot_prob, neighbor_prob, com_prob;
    double p_trans;
    double age_susceptibility;
    boolean infected_today, recovered_today, identified_today, died_today, severe_today, contact_reported_today, rnd_schedule_today, nojob;
    boolean infected_neighborhood, infected_spot, infected_community;
    boolean isInitialSeed;
    double incubation_period, recovery_period, severe_period, identify_period, quarantine_period;
    int day_spot, day_type, night_spot, day_cluster, night_cluster, rnd_spot, rnd_cluster;
    int day_neighbor, night_neighbor, rnd_neighbor;
    int day_com, night_com, rnd_com;
    int og_day_com, og_day_neighbor, og_day_cluster, og_day_spot, og_day_type;
    int day_track, night_track, rnd_track;
    int s_type;
    int day_bubble_small, day_bubble_medium, day_bubble_large;
    int infection_location, contact_location;
    boolean PA_satisfied_s12, PA_satisfied_s3, PA_satisfied_s5;
    
    public Agent(){
        p_trans =  0.208;
        infected_today = recovered_today = identified_today = rnd_schedule_today = died_today = severe_today = contact_reported_today = infected_community = infected_neighborhood = infected_spot = isInitialSeed = false;
        nojob = false;
        dt = 0.5;
        asymptomatic_period = 0; infected_period = 0; infectiousness = 0; //vLoad = 0; vLoadType = 0;
        spot_prob = neighbor_prob = com_prob = 0;
        spot_prob = 1;
        day_spot = day_type = night_spot = day_cluster = night_cluster = rnd_spot = rnd_cluster = 0;
        day_neighbor = night_neighbor = rnd_neighbor = 0;
        day_com = night_com = rnd_com = 0;
        day_track = night_track = rnd_track = 0;
        incubation_period=0;
        recovery_period=50;
        severe_period=0;
        identify_period=0;
        quarantine_period = 0;
        status = 0;
        age_susceptibility = 0;
        s_type = 0;
        day_bubble_small = day_bubble_medium = day_bubble_large = 0;
        infection_location = contact_location = 999;
    }

    public boolean is_same_neighbor(Agent target, int run_type){
        // night
        if(run_type == 1){
            if(target.night_neighbor == this.night_neighbor){
                return true;
            }else{
                return false;
            }
        }
        else{
            if(target.day_neighbor == this.day_neighbor){
                return true;
            }else{
                return false;
            }
        }
    }

    public boolean is_same_community(Agent target, int run_type){
        if(run_type == 1){// night
            if(target.night_com == this.night_com) return true;
            else
                return false;
        }
        else{
            if(target.day_com == this.day_com) return true;
            else 
                return false;
        }
    }

    public boolean is_same_cluster(Agent target, int run_type){
        if(run_type == 1){// night
            if(target.night_cluster == this.night_cluster) return true;
            else 
                return false;
        }else{
            if(target.day_type == 0 && this.day_type == 0){
                if(target.night_cluster == this.night_cluster){
                    return true;
                }else{
                    return false;
                }
            }else{
                if(target.day_type == this.day_type){
                    return true;
                }else{
                    return false;
                }
            }
        }
    }

    public boolean is_same_spot(Agent target, int run_type){
        if(run_type == 1){// night
            if((target.night_cluster == this.night_cluster) && (target.night_spot == this.night_spot)) return true;
            else
                return false;
        }else{
            if(target.day_type == 0 && this.day_type == 0){
                if((target.night_cluster == this.night_cluster)&&(target.day_spot == this.day_spot)){
                    return true;
                }else{
                    return false;
                }
            }else{
                if((target.day_type == this.day_type)&&(target.day_spot == this.day_spot)){
                    return true;
                }else{
                    return false;
                }
            }
        }
    }

    public void set_agent(int id, int type){
        agent_id = id;
        age_type = type;
    }

    public void setStatus(int stat){
        status = stat;
    }


    // update disease status: S_I_R model
    public void update_status(){
        spot_prob = neighbor_prob = com_prob = 1;
        switch(status){
            case 0:
                status = change_state0();       // 미감염 susceptible 업데이트
                break;
            case 1:
                infected_period += dt;
                status = change_state1();       // 무증상 감염자 업데이트       
                break;
            case 2:
                infected_period += dt;
                status = change_state2();       // 유증상 감염자 업데이트
                break;
            case 3:
                infected_period += dt;
                status = change_state3();       // 확진자 업데이트
                break;
            case 4:
                infected_period += dt;
                status = change_state4();       // 경증환자 업데이트
                break;
            case 5:
                infected_period += dt;
                status = change_state5();       // 중증환자 업데이트
                break;
            case 6:
                infected_period += dt;
                status = change_state6();       // ICU 중증환자 업데이트
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                status = change_state9();       // 미감염 susceptible 업데이트
                break;
            default:
                System.out.println("there is an error: unexpected status in Agent class");
        }
    }

    public void set_neighbor_prob(double prob){
        neighbor_prob = prob;
    }

    public void set_community_prob(double prob){
        com_prob = prob;
    }

    public void set_day_community(int day_c){
        day_com = day_c; 
    }

    public void set_night_community(int night_c){
        night_com = night_c;
    }

    public void set_day_neighbor(int day_n){
        day_neighbor = day_n; 
    }

    public void set_home_neighbor(int night_n){
        night_neighbor = night_n;
    }

    public void set_cluster(int night_cluster){
        this.night_cluster = night_cluster;
    }

    public void set_day_cluster(int day_cluster){
        this.day_cluster = day_cluster;
    }

    public void set_day_spot(int type, int day_s){
        day_type = type; //7 workplace
        day_spot = day_s; //id
    }

    public void set_home(int night_s){
        night_spot = night_s;
    }

    public void set_bubble_small(int day_b_small){
        this.day_bubble_small = day_b_small; 
    }

    public void set_bubble_medium(int day_b_medium){
        this.day_bubble_medium = day_b_medium;
    }

    public void set_bubble_large(int day_b_large){
        this.day_bubble_large = day_b_large;
    }

    public void set_og_day_type(int day_type, int day_spot){
        this.og_day_type = day_type;
        this.og_day_spot = day_spot;
    }

    public double age_dependent_susceptibility(int s_type){
        if(s_type == 0 || s_type == 1){
            age_susceptibility = 1.0;
        }else if(s_type == 2 || s_type == 3){
            age_susceptibility = 2.0;
        }else if(s_type == 4){
            age_susceptibility = 3.5;
        }
        return age_susceptibility;
    }


    // 미감염 susceptible 업데이트

    private int change_state0(){
        // 감염된 경우
        if(infected_period >= 1 ){
            this.infected_today = true;

            // 만약 감염되었다면 incubation period를 지정해줌   (증상발현까지 걸리는 시간)
            double r = Math.random();
            r = Math.round(r*1000)/1000.0;
            double incub_prob_array[] = {0, 0.034, 0.166, 0.334, 0.489, 0.614, 0.711, 0.783, 0.837, 0.877, 0.907, 0.929, 0.945, 0.957, 0.967, 0.974, 0.980, 0.984, 0.987, 0.990, 1};
            for(int i=0;i<19;i++){
                if(incub_prob_array[i]<=r && r<incub_prob_array[i+1]){
                    this.incubation_period = (i+1); //원래 +1
                }
            }

            // 만약 감염되었다면 recovery period를 지정해줌     (완치까지 걸리는 시간)
            r = Math.random();
            r = Math.round(r*1000)/1000.0;
            double recovery_prob_array[] = {0, 0, 0.054, 0.299, 0.606, 0.765, 0.865, 0.919, 0.968, 1}; //레퍼런스에서 경증(중에서 무증상) 완치기간 사용
            for(int i=1;i<9;i++){
                if(recovery_prob_array[i]<=r && r<recovery_prob_array[i+1] ){
                    this.recovery_period = (i*5);  //원래 *5
                }
            }
            return 1;
        }
        else return 0;
    }

    // 무증상 감염자 업데이트
    private int change_state1(){
        double r = Math.random();

        // 확진없이 완치되는 경우
        if(this.infected_period >= this.recovery_period){
            this.recovered_today = true;
            return 7;

        // 밀접접촉자로 분류되지 않고 확진되는 경우
        }else{
            if(r<0.024){
                return 3;
            }
        }

        // 증상이 발현되는 경우
        if(infected_period - 1 >= this.incubation_period){
            // 만약 증상이 발현되었다면 identify period를 지정해줌   
            double sym_isolation_prob_array[] = {0, 0.045, 0.23, 0.44, 0.59, 0.69, 0.76, 0.81, 0.86, 0.90, 0.92, 0.94, 0.96, 0.97, 0.98, 0.99, 1};
            for(int i=0;i<16;i++){
                if(sym_isolation_prob_array[i]<=r && r<sym_isolation_prob_array[i+1] ){
                    this.identify_period=i+1;} // 5* 원래없음
            }
            return 2;
            
        }else {
            infected_period += 0.5;
            return 1;
        }
    }

    // 유증상 감염자 업데이트
    private int change_state2(){
        double r = Math.random();
        // 경증환자 완치
        if(this.infected_period >= this.recovery_period){
            this.recovered_today = true;
            return 7;//완치
        
        // 확진
        }else if(this.infected_period >= this.identify_period){
            this.identified_today = true;
            return 3;//확진

        // 고령자 중증 전환(확진 전)
        }else if((this.infected_period>2) && (this.age_type == 4)){
            if(r <= 0.0013){       // 고령자 daily 중증전환률
                this.infected_period = 0;
                this.identified_today = true;
                this.severe_today = true;
                return 5;       // 중증 전환
            }else{
                return 2;
            }

        // 비고령자 중증 전환(확진 전)
        }else if(this.infected_period>2){
            if(r <= 0.0006){    // 비고령자 daily 중증전환률
                this.infected_period = 0;
                this.identified_today = true;
                this.severe_today = true;
                return 5;       // 중증 전환
            }else{
                return 2;
            }

        }else{
                infected_period += dt;
                return 2;
        }
    }

    // 확진자 bed_type에 따라 state 배정
    private int change_state3(){
        switch(this.bed_type){
            case 0:
                return 3;
            case 1://자가격리 배정
                return 4;
            case 2://Tcenter 배정
                return 4;
            case 3://Genbed 배정
                return 4;
            default:
                System.out.println("there is an error: unexpected bed_type in Agent class");
        }
        return 3;
    }

    // 경증환자 업데이트
    private int change_state4(){
        double r = Math.random();

        // 고령자 중증전환
        if(this.age_type==4){
            switch(this.bed_type){
                case 0:
                    System.out.println("Error : status 4 with bed type 0");
                    return 3;
                case 1://자가격리 배정
                    if(r <= 0.005){       // 고령자 withdraw daily 중증전환률
                        this.infected_period = 0;
                        this.severe_today = true;
                        return 5;       // 중증 전환
                    }break;
                case 2://Tcenter 배정
                    if(r <= 0.002){      // 고령자 Tcenter daily 중증전환률
                        this.infected_period = 0;
                        this.severe_today = true;
                        return 5;       // 중증 전환
                    }break;
                case 3://Genbed 배정
                    if(r <= 0.0013){    // 고령자 Genbed daily 중증전환률
                        this.infected_period = 0;
                        this.severe_today = true;
                        return 5;       // 중증 전환
                    }break;
                default:
                    System.out.println("there is an error: unexpected bed_type in Agent class");
            }

        // 비고령자 중증전환
        }else{
            if(r <= 0.0006){
                this.infected_period = 0;
                this.severe_today = true;
                return 5;
            }
        }

        // 경증환자 완치
        if(this.infected_period >= this.recovery_period){
            this.recovered_today = true;
            // System.out.println("Recovered");
            return 7;//완치
        }
        return 4;
    }

    // 중증환자 업데이트
    private int change_state5(){
        this.severe_today=true;
        // ICU state 보내기
        if(this.bed_type == 4){
            return 6;

        // Untreated severe는 사망
        }else if(this.bed_type == 5){
            this.died_today=true;
            return 8;
        }
        return 5;
    }

    // ICU 중증환자 업데이트
    private int change_state6(){
        double r = Math.random();
        // 24일 후 완치 혹은 사망
        if(this.infected_period>=24){
            if(r<0.5181){
                this.died_today=true;
                return 8;
            }else{
                this.recovered_today=true;
                return 7;
            }
        }else{
            return 6;
        }
    }

    // 자가격리자 업데이트
    private int change_state9(){
        if(this.infected_period >= 1){
            double r2 = Math.random();
            r2 = Math.round(r2*1000)/1000.0;
            if(r2<0.9){
                this.quarantine_period = 0;
                this.identified_today = true;
                return 3;
            }
        }
        this.quarantine_period += 0.5;
        return 9;
    }
}
