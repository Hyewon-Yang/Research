import java.util.ArrayList;
import java.util.Iterator;

public class Community {
    int community_id, max_ppl;
    ArrayList<Neighborhood> neighbor;
    ArrayList<Agent> ppl;
    ArrayList<Spot> play_group, daycare, elementary, middle, high, workgroup, pub_facilities;
    ArrayList<Spot> h_cluster;
    ArrayList<Spot> b_cluster_small, b_cluster_medium, b_cluster_large;
    int numOfppl, numOfNeighbor, numOfPlaygroup, numOfDaycare, numOfElement, numOfMiddle, numOfHigh, numOfWork, numOfPubfacility;//, numOfInfection, numOfNewInfection;
    int numOfHousehold, numOfCluster;
    int numOfHospital, numOfTcenter, numOfGenhospital;  
    int numOfICUBed; // 남은 ICU bed 수
    int numOfTCRoom; // 남은 Treatment 방 수
    int numOfGenbed;
    int numOfBubble_small, numOfBubble_medium, numOfBubble_large;
    int Assigned_ICU_Beds; // 커뮤니티에 배정된 bed 수
    int Assigned_Tcenters; // 커뮤니티에 배정된 Treatment Center 수
    int Assigned_Genhospitals; // 커뮤니티에 배정된 General hospital 수
    int Assigned_PublicFacilities; // 커뮤니티에 배정된 다중이용시설 수
    double c_time;
    double open_time;
    Disease disease;
    Main m;

    public Community(int numppl){
        max_ppl = numppl;
        initialize();
    }

    // remove ppl during day/night time for next day/night schedule
    public void shift(){
        ppl = new ArrayList<Agent>();
        Iterator iter = neighbor.iterator();
        while(iter.hasNext()){
            Neighborhood n = (Neighborhood)iter.next();
            n.shift();
        }
        iter = h_cluster.iterator();
        sub_shift(iter);
        iter = play_group.iterator();
        sub_shift(iter);
        iter = daycare.iterator();
        sub_shift(iter);
        iter = elementary.iterator();
        sub_shift(iter);
        iter = middle.iterator();
        sub_shift(iter);
        iter = high.iterator();
        sub_shift(iter);
        iter = workgroup.iterator();
        sub_shift(iter);
        iter = b_cluster_small.iterator();
        sub_shift(iter);
        iter = b_cluster_medium.iterator();
        sub_shift(iter);
        iter = b_cluster_large.iterator();
        sub_shift(iter);
    }

    public void add_neighborhood(Neighborhood new_neigh){
        new_neigh.neighbor_id = numOfNeighbor;
        neighbor.add(new_neigh);
        numOfNeighbor++;
    }

    public void add_agent(Agent new_agent){
        ppl.add(new_agent);
    }

    // consider only suscpeible group and infected group
    // assign infected prob. to susceptible agents in this community
    // and call disease_update() in neigborhoods of this community

     public void disease_update(int day_type, int scenario_num_now, int bubble_size_now, double accepted_range_now){
        Iterator iter = ppl.iterator();
        ArrayList<Agent> s_group = new ArrayList<Agent>(); // S, I 그룹별로 따로 모아서 처리
        ArrayList<Agent> i_group = new ArrayList<Agent>();

        while(iter.hasNext()){
            Agent next = (Agent)iter.next();

            if(next.status==0 && next.infected_today == false){
                s_group.add(next);
            }else if(next.status<=2 && next.infected_today == false){
                i_group.add(next);
                }if(next.bed_type == 1){
                next.day_neighbor = next.night_neighbor;
                next.day_cluster = next.night_cluster;
                next.day_spot = next.night_spot;
                next.day_type = 0;
            }
            
            // 1. 중증환자 ICU assign
            if(next.severe_today=true && next.status==5){
                switch(next.bed_type){
                    case 0:
                        break;
                    case 1://자가격리에서 중증 전환
                        break;
                    case 2://Tcenter에서 중증 전환
                        this.numOfTCRoom ++; //TCroom 수가 하나 늘어남
                        break;
                    case 3://Genbed에서 중증 전환
                        this.numOfGenbed ++;
                        break;
                    case 5:
                        break;
                    default:
                        System.out.println(next.bed_type);
                        System.out.println("severe case: there is an error: unexpected bed_type in Agent class");
                }

                // 중환자 병상배정
                if(this.numOfICUBed > 0) {  //ICU bed 가 있으면
                    this.numOfICUBed --; //배정
                    next.bed_type = 4;
                    Spot Assigned_hospital;
                    int hospital_id = numOfCluster + numOfHospital;
                    int bed_id = 999;
                    for(int hos = 0; hos < numOfHospital ; hos ++){
                        if(bed_id == 999){
                            hospital_id = numOfCluster + numOfHospital - 1  - hos; // 뒤에서부터 사용
                            Assigned_hospital = this.h_cluster.get(hospital_id);
                            ArrayList<Spot> Assigned_bed = Assigned_hospital.homes;
                            Iterator iter_beds = Assigned_bed.iterator();
                            bed_id = find_bed(iter_beds);
                        }else{
                            break;
                        }
                    }

                    if(bed_id == 999){
                        System.out.println("No bed");
                        System.out.println(this.numOfICUBed);
                        System.out.println(hospital_id);
                    }
                    
                    next.day_com = next.night_com = this.community_id;
                    next.day_cluster = next.night_cluster = hospital_id;
                    next.day_spot = next.night_spot = bed_id;
                    next.day_type = 10;

                // 병상없는 중환자
                }else{
                    next.bed_type=5;
                }
                next.severe_today=false;

            // 2. Recovered bed 반납
            }else if(next.status==7){
                switch(next.bed_type){
                    case 0:
                        break;
                    case 1://자가격리에서 중증 전환
                        break;
                    case 2://Tcenter에서 중증 전환
                        this.numOfTCRoom ++;
                        break;
                    case 3://Genbed에서 중증 전환
                        this.numOfGenbed ++;
                        break;
                    case 4:
                        Spot hospital = h_cluster.get(next.day_cluster);
                        hospital.homes.get(next.day_spot).occupied = 0;
                        this.numOfICUBed++;
                        break;
                    case 5:
                        break;
                    default:
                        System.out.println("there is an error: unexpected bed_type in Agent class");
                }
                next.bed_type=0;

            // 3. Died bed 반납
            }else if(next.status==8 && next.bed_type==4){
                Spot hospital = h_cluster.get(next.day_cluster);
                hospital.homes.get(next.day_spot).occupied = 0;
                next.bed_type=0;
                this.numOfICUBed++;
            
            // 4. 확진자 genbed assign
            }else if(next.status==3 && this.numOfGenbed > 0){ 
                this.numOfGenbed --;
                next.bed_type=3;
                int Genhospital_id = numOfCluster + numOfHospital + numOfTcenter + numOfGenhospital - 1;
                int genhos = (int) Math.floor(numOfGenbed/this.h_cluster.get(Genhospital_id).homes.get(0).max_pplNum); 
                Genhospital_id = numOfCluster + numOfHospital + numOfTcenter + numOfGenhospital - 1  - genhos; // 앞에서부터 사용     
                next.day_com = next.night_com = this.community_id;        
                next.day_cluster = next.night_cluster = Genhospital_id;
                next.day_spot = next.night_spot = 0; // Assume general hospital cluster has only one general hospital 
                next.day_type = 15;
            
            // 5. 확진자 Tcenter bed assign
            }else if(next.status==3 && this.numOfTCRoom > 0) {
                this.numOfTCRoom --;
                System.out.println("Error : infected in TC center");
                next.bed_type=2;
                int Tcenter_id = numOfCluster + numOfHospital + numOfTcenter - 5;
                int center = (int) Math.floor(numOfTCRoom/this.h_cluster.get(Tcenter_id).homes.get(0).max_pplNum); 
                Tcenter_id = numOfCluster + numOfHospital + numOfTcenter - 1  - center; // 앞에서부터 사용 
                next.day_com = next.night_com = this.community_id;        
                next.day_cluster = next.night_cluster = Tcenter_id;
                next.day_spot = next.night_spot = 0; // Assume treatment center cluster has only one center 
                next.day_type = 12;

            // 6. 확진자 자가격리
            }else if(next.status==3 && this.numOfTCRoom == 0) { 
                System.out.println("Error : infected withdraw");
                next.bed_type=1;
                next.day_com = next.night_com;        
                next.day_neighbor = next.night_neighbor;
                next.day_cluster = next.night_cluster;
                next.day_spot = next.night_spot;
                next.day_type = 0;
            }
        }

        Iterator iter_i = i_group.iterator();

        while(iter_i.hasNext()){
            Iterator iter_s = s_group.iterator();
            Agent infected = (Agent)iter_i.next();
            double infected_prob, contact_prob;

            if(infected.day_type!=1 && day_type != 1){
                while(iter_s.hasNext()){
                    Agent susceptible = (Agent)iter_s.next();
                    if(susceptible.status == 0){
                        if(susceptible.PA_satisfied_s12 == true || susceptible.PA_satisfied_s3 == true || susceptible.PA_satisfied_s5 == true){
                            contact_prob = disease.calculate_contact_prob_revised(susceptible.age_type, infected.age_type, 9, scenario_num_now, bubble_size_now, accepted_range_now);
                        }
                        else{
                            contact_prob = disease.calculate_contact_prob(susceptible.age_type, infected.age_type, 9);
                        }
                        double q = Math.random();
                        if (q < contact_prob && susceptible.status == 0){
                            susceptible.contact_location = 9;

                            infected_prob = disease.p_trans;
                            // infected_prob = contact_prob * disease.p_trans;
                            infected_prob = infected_prob * susceptible.age_dependent_susceptibility(susceptible.age_type);
                            
                            double r = Math.random();
                            if(r < infected_prob && susceptible.status == 0){
                                susceptible.infected_period = 1;
                                susceptible.infected_today = true;
                                susceptible.infectiousness = 1;
                                susceptible.infected_community = true;
                                susceptible.infection_location = 9;
                            }
                        }
                    }
                }
            }
            // else{ //scenario 1 & 2 & 4
            //     if(infected.day_type!=1 && day_type != 1){
            //         while(iter_s.hasNext()){
            //             Agent susceptible = (Agent)iter_s.next();
            //             if(susceptible.status == 0 && susceptible.day_type != 17 && susceptible.day_type != 18 && susceptible.day_type != 19){
            //                 contact_prob = disease.calculate_contact_prob(susceptible.age_type, infected.age_type, 9);
            //                 double q = Math.random();
            //                 if (q < contact_prob && susceptible.status == 0){
            //                     susceptible.contact_location = 9;

            //                     infected_prob = disease.p_trans;
            //                     // infected_prob = contact_prob * disease.p_trans;
            //                     infected_prob = infected_prob * susceptible.age_dependent_susceptibility(susceptible.age_type);
                        
            //                     double r = Math.random();
            //                     if(r < infected_prob/120 && susceptible.status == 0){
            //                         susceptible.infected_period = 1;
            //                         susceptible.infected_today = true;
            //                         susceptible.infectiousness = 1;
            //                         susceptible.infected_community = true;
            //                         susceptible.infection_location = 9;
            //                     }
            //                 }
            //             }
            //         }
            //     }
            // }     
        }

        iter = neighbor.iterator();

        while(iter.hasNext()){
            Neighborhood n = (Neighborhood)iter.next();
            if(day_type == 0){
                n.disease_update(day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
        }

        iter = h_cluster.iterator();
        while(iter.hasNext()){
            Spot s = (Spot)iter.next();
            s.disease_update(1, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
        }
        
        if(day_type == 0){
            iter = play_group.iterator();
            while(iter.hasNext()){
                Spot s = (Spot)iter.next();
                s.disease_update(2, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = daycare.iterator();
            while(iter.hasNext()){
                Spot s = (Spot)iter.next();
                s.disease_update(3, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = elementary.iterator();
            while(iter.hasNext()){
                Spot s = (Spot)iter.next();
                s.disease_update(4, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = middle.iterator();
            while(iter.hasNext()){
                Spot s = (Spot)iter.next();
                s.disease_update(5, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = high.iterator();
            while(iter.hasNext()){
                Spot s = (Spot)iter.next();
                s.disease_update(6, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = workgroup.iterator();
            while(iter.hasNext()){
                Spot s = (Spot)iter.next();
                s.disease_update(7, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = b_cluster_small.iterator();
            while(iter.hasNext()){ 
                Spot s = (Spot)iter.next();
                s.disease_update(17, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = b_cluster_medium.iterator();
            while(iter.hasNext()){ 
                Spot s = (Spot)iter.next();
                s.disease_update(18, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
            iter = b_cluster_large.iterator();
            while(iter.hasNext()){ 
                Spot s = (Spot)iter.next();
                s.disease_update(19, day_type, scenario_num_now, bubble_size_now, accepted_range_now);
            }
        }
    }

    public void add_spot(Spot new_spot, int type){
        switch(type){
            case 1: // household cluster
                new_spot.spot_id = numOfCluster++;
                h_cluster.add(new_spot);
                break;
            case 2: // (small) paly group
                new_spot.spot_id = numOfPlaygroup++;
                play_group.add(new_spot);
                break;
            case 3: // (large) daycare
                new_spot.spot_id = numOfDaycare++;
                daycare.add(new_spot);
                break;
            case 4: // elementary school
                new_spot.spot_id = numOfElement++;
                elementary.add(new_spot);
                break;
            case 5: // middle school
                new_spot.spot_id = numOfMiddle++;
                middle.add(new_spot);
                break;
            case 6: // high school
                new_spot.spot_id = numOfHigh++;
                high.add(new_spot);
                break;
            case 7: // workgroup
                new_spot.spot_id = numOfWork++;
                workgroup.add(new_spot);
                break;
            case 8:
            case 9:
                System.out.println("Wrong type insertion: add_spot() doesn't allow neighborhood/community type");
                break;
            case 10: // bed
                System.out.println("Wrong type insertion: add_spot() doesn't allow neighborhood/community type");
                break;
            case 11: // hospital
                new_spot.spot_id = numOfCluster + numOfHospital; // 생성 순서 주의
                numOfHospital++;
                h_cluster.add(new_spot);
                numOfICUBed = numOfICUBed + new_spot.homes.size();
                break;
            case 12: // room
                System.out.println("Wrong type insertion: add_spot() doesn't allow neighborhood/community type");
                break;
            case 13: // Treatment center cluster
                new_spot.spot_id = numOfCluster + numOfHospital + numOfTcenter; // 생성 순서 주의
                numOfTcenter++;
                h_cluster.add(new_spot);
                numOfTCRoom = numOfTCRoom + new_spot.homes.get(0).max_pplNum;
                break;
            case 14: // Publicly used facilities
                new_spot.spot_id = numOfPubfacility++;
                pub_facilities.add(new_spot);
                break;
            case 15: // General hospital
                System.out.println("Wrong type insertion: add_spot() doesn't allow neighborhood/community type");
                break;
            case 16: // General hospital cluster
                new_spot.spot_id = numOfCluster + numOfHospital + numOfTcenter + numOfGenhospital; // 생성 순서 주의
                numOfGenhospital++;
                h_cluster.add(new_spot);
                numOfGenbed = numOfGenbed + new_spot.homes.get(0).max_pplNum;
                break;
            case 17: // Bubble_small
                new_spot.spot_id = numOfBubble_small++;
                b_cluster_small.add(new_spot);
                break;
            case 18: // Bubble_medium
                new_spot.spot_id = numOfBubble_medium++;
                b_cluster_medium.add(new_spot);
                break;
            case 19: // Bubble_large
                new_spot.spot_id = numOfBubble_large++;
                b_cluster_large.add(new_spot);
                break;
            default:
                System.out.println("There is an type error: wrong type in add_sopt() in Neighborhood class");
        }
    }

     public ArrayList<Spot> get_spot(int type){
        switch(type){
            case 2: return play_group;
            case 3: return daycare;
            case 4: return elementary;
            case 5: return middle;
            case 6: return high;
            case 7: return workgroup;
            case 14: return pub_facilities;
            default:
                System.out.println("There is an error: get_spot() in community class"); return null;
        }
    }

    private void initialize(){
        neighbor = new ArrayList<Neighborhood>();
        ppl = new ArrayList<Agent>();
        disease = new Disease();
        m = new Main();
        h_cluster = new ArrayList<Spot>();
        b_cluster_small = new ArrayList<Spot>();
        b_cluster_medium = new ArrayList<Spot>();
        b_cluster_large = new ArrayList<Spot>();
        play_group = new ArrayList<Spot>();
        daycare = new ArrayList<Spot>();
        elementary = new ArrayList<Spot>();
        middle = new ArrayList<Spot>();
        high = new ArrayList<Spot>();
        workgroup = new ArrayList<Spot>();
        pub_facilities = new ArrayList<Spot>();
        numOfppl = numOfNeighbor = 0;
        numOfPlaygroup = numOfDaycare = numOfElement = numOfMiddle = numOfHigh = numOfWork = 0;
        numOfPubfacility = 1;
        numOfHousehold = numOfCluster =  0;
        numOfBubble_small = numOfBubble_medium = numOfBubble_large = 0;
        numOfICUBed = numOfTCRoom = 0;
        numOfHospital = numOfTcenter = numOfGenhospital = 0;
        Assigned_ICU_Beds = 100000;    // ICU bed 초개 갯수
        Assigned_Tcenters = 1; // Assigned Tcenter beds = Assigned_Tcenters*1000
        Assigned_Genhospitals = 1; // Assigned Genhospital capacity = Assigned_Genhospitals*1000
        Assigned_PublicFacilities = 10;
        c_time = 0.0;
        open_time = 5.0;
    }

    private void sub_shift(Iterator iter){
        while(iter.hasNext()){
            Spot s = (Spot)iter.next();
            s.shift(s.spot_type);
        }
    }
    private int find_bed(Iterator iter){
        int bed_id = 0;
        while(iter.hasNext()){
            Spot s = (Spot)iter.next();
            if(s.occupied == 0){
                s.occupied = 1;
                return bed_id;
            }else{
                bed_id++;
            }
        }
        return 999;
    }
}