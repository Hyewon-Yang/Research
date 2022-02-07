public class Disease {
    double contact_prob, p_trans;
    int susceptible_type, infectious_type, group_type;

    public Disease(){
        p_trans =  0.208;// R0 1.23 (daejeon)
    }
    
    public double calculate_contact_prob(int s_type, int i_type, int g_type){
        contact_prob = 0;
        switch(g_type){
            case 0: // household
                if(i_type == 0 || i_type == 1) //infectious = child
                {
                    if(s_type == 0 || s_type == 1) contact_prob = 0.8;
                    else if( s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.35;
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                    if(s_type == 0 || s_type == 1) contact_prob = 0.25;
                    else if(s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.4;
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else System.out.println("there is a type error: g_type 0(household) in disease class");
                break;
            case 1: // household cluster
                if(i_type == 0 || i_type == 1) //infectious =child
                {
                    if(s_type == 0 || s_type == 1) contact_prob = 0.08;
                    else if( s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.035;
                    else System.out.println("there is a type error: g_type 1(h_cluster) in disease class");
                }
                else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                    if(s_type == 0 || s_type == 1) contact_prob = 0.025;
                    else if( s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.04;
                    else System.out.println("there is a type error: g_type 1(h_cluster) in disease class");
                }
                else System.out.println("there is a type error: g_type 1(h_cluster) in disease class");
                break;
            case 2: // (small) play group
                contact_prob = 0.28;
                break;
            case 3: // (large) daycare
                contact_prob = 0.12;
                break;
            case 4: // elementary school
                contact_prob = 0.0348;
                break;
            case 5: // middle school
                contact_prob = 0.03;
                break;
            case 6: // high school
                contact_prob = 0.0252;
                break;
            case 7: // workgroup
                contact_prob = 0.05;
                break;
            case 8: // neighborhood
                switch(s_type){
                    case 4: contact_prob = 0.0000087; break;
                    case 3: contact_prob = 0.00000435; break;
                    case 2: contact_prob = 0.00000435; break;
                    case 1: contact_prob = 1.63125E-06; break;
                    case 0: contact_prob = 5.4375E-07; break;
                    default: System.out.println("there is a type error: g_type 8(neighborhood) in disease class");
                }
                break;
            case 9: // community
                switch(s_type){
                    case 4: contact_prob = 0.00000435; break;
                    case 3: contact_prob = 0.000002175; break;
                    case 2: contact_prob = 0.000002175; break;
                    case 1: contact_prob = 0.000000815; break;
                    case 0: contact_prob = 2.725E-07; break;
                    default: System.out.println("there is a type error: g_type 8(neighborhood) in disease class");
                }
                break;
            case 10: // Bed
                break;
            case 14: // publicly used facilities
                contact_prob = 0.21;
                break;
            case 17: // Bubble
                if(i_type == 0 || i_type == 1) //infectious = child
                    {
                        if(s_type == 0 || s_type == 1) contact_prob = 0.8;
                        else if( s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.35;
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                        if(s_type == 0 || s_type == 1) contact_prob = 0.25;
                        else if(s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.4;
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                    break;
            case 18: // Bubble
                if(i_type == 0 || i_type == 1) //infectious = child
                    {
                        if(s_type == 0 || s_type == 1) contact_prob = 0.8;
                        else if( s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.35;
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                        if(s_type == 0 || s_type == 1) contact_prob = 0.25;
                        else if(s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.4;
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                    break;
            case 19: // Bubble
                if(i_type == 0 || i_type == 1) //infectious = child
                    {
                        if(s_type == 0 || s_type == 1) contact_prob = 0.8;
                        else if( s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.35;
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                        if(s_type == 0 || s_type == 1) contact_prob = 0.25;
                        else if(s_type == 2 || s_type == 3 || s_type == 4 ) contact_prob = 0.4;
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                    break;
            default:
                System.out.println("there is a type error: group type in disease model");
        }
        return contact_prob;
    }

    public double calculate_contact_prob_revised(int s_type, int i_type, int g_type, int scenario_num_now, int bubble_size_now, double accepted_range_now){
        contact_prob = 0;
        switch(g_type){
            case 0: // household
                if(i_type == 0 || i_type == 1) //infectious = child
                {
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 1.321;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.77;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.544;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.42;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.342;
                        else if(scenario_num_now == 3) contact_prob = 0.858;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.8;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 1.168;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 1.048;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.95;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.868;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.996;
                    }else if( s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 0.578;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.337;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.238;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.184;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.15;
                        else if(scenario_num_now == 3) contact_prob = 0.376;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.35;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.511;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.458;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.416;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.38;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.436;
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 0.413;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.241;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.17;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.131;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.107;
                        else if(scenario_num_now == 3) contact_prob = 0.268;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.25;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.365;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.327;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.297;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.271;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.311;
                    }else if(s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 0.66;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.385;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.272;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.21;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.171;
                        else if(scenario_num_now == 3) contact_prob = 0.429;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.4;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.584;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.524;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.475;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.434;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.498;
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else System.out.println("there is a type error: g_type 0(household) in disease class");
                break;
            case 1: // household cluster
                if(i_type == 0 || i_type == 1) //infectious =child
                {
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 0.132;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.077;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.054;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.042;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.034;
                        else if(scenario_num_now == 3) contact_prob = 0.086;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.08;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.117;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.105;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.095;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.087;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.100;
                    }else if( s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 0.058;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.034;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.024;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.018;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.015;
                        else if(scenario_num_now == 3) contact_prob = 0.038;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.035;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.051;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.046;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.042;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.038;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.044;
                    }
                    else System.out.println("there is a type error: g_type 1(h_cluster) in disease class");
                }
                else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 0.041;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.024;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.017;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.013;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.011;
                        else if(scenario_num_now == 3) contact_prob = 0.027;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.025;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.037;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.033;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.03;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.027;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.031;
                    }
                    else if( s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 1 || (scenario_num_now == 2 && bubble_size_now == 1) || (scenario_num_now == 5 && accepted_range_now == 0.0)) contact_prob = 0.066;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.039;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.027;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.021;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.017;
                        else if(scenario_num_now == 3) contact_prob = 0.043;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.04;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.058;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.052;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.047;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.043;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.050;
                    }
                    else System.out.println("there is a type error: g_type 1(h_cluster) in disease class");
                }
                else System.out.println("there is a type error: g_type 1(h_cluster) in disease class");
                break;
            case 2: // (small) play group
                if(scenario_num_now == 3) contact_prob = 0.2836;
                else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.28;
                else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.2446;
                else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.2562;
                else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.2656;
                else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.2734;
                else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.2611;
                else contact_prob = 0.0;
                break;
            case 3: // (large) daycare
                if(scenario_num_now == 3) contact_prob = 0.1236;
                else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.12;
                else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.0846;
                else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.0962;
                else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.1056;
                else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.1134;
                else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.1011;
                else contact_prob = 0.0;                
                break;
            case 4: // elementary school
                if(scenario_num_now == 3) contact_prob = 0.0384;
                else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.0348;
                else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.011;
                else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.0204;
                else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.0282;
                else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.0159;
                else contact_prob = 0.0;                  
                break;
            case 5: // middle school
                if(scenario_num_now == 3) contact_prob = 0.0336;
                else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.03;
                else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.0062;
                else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.0156;
                else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.0234;
                else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.0111;
                else contact_prob = 0.0;                 
                break;
            case 6: // high school
                if(scenario_num_now == 3) contact_prob = 0.0288;
                else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.0252;
                else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.0014;
                else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.0108;
                else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.0186;
                else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.0063;
                else contact_prob = 0.0;    
                break;
            case 7: // workgroup
                if(scenario_num_now == 3) contact_prob = 0.0536;
                else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.05;
                else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 0.0146;
                else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 0.0262;
                else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 0.0356;
                else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 0.0434;
                else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 0.0311;
                else contact_prob = 0.0;    
                break;
            case 8: // neighborhood
                switch(s_type){
                    case 4: 
                        if(scenario_num_now == 3) contact_prob = 9.33414E-06;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.0000087;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 2.54139E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 4.55797E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 6.19708E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 7.55564E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 5.41776E-06;
                        else contact_prob = 0.0; break;
                    case 3:
                        if(scenario_num_now == 3) contact_prob = 4.66707E-06;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.00000435;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 1.2707E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 2.27898E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 3.09854E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 3.77782E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 2.70888E-06;
                        else contact_prob = 0.0; break;
                    case 2: 
                        if(scenario_num_now == 3) contact_prob = 4.66707E-06;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.00000435;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 1.2707E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 2.27898E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 3.09854E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 3.77782E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 2.70888E-06;
                        else contact_prob = 0.0; break;                  
                    case 1:
                        if(scenario_num_now == 3) contact_prob = 1.75015E-06;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 1.63125E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 4.76511E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 8.54619E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 1.16195E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 1.41668E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 1.01583E-06;
                        else contact_prob = 0.0; break;
                    case 0:
                        if(scenario_num_now == 3) contact_prob = 5.83383E-07;
                        else if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 5.4375E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 1.58837E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 2.84873E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 3.87318E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 4.72228E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 3.3861E-07;
                        else contact_prob = 0.0; break;
                    default: System.out.println("there is a type error: g_type 8(neighborhood) in disease class");
                }
                break;
            case 9: // community
                switch(s_type){
                    case 4: 
                        if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.00000435;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 1.2707E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 2.27898E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 3.09854E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 3.77782E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 2.70888E-06;
                        else contact_prob = 0.0; break;
                    case 3:
                        if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.000002175;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 6.35348E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 1.13949E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 1.54927E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 1.88891E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 1.35444E-06;
                        else contact_prob = 0.0; break;                    
                    case 2:
                        if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.000002175;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 6.35348E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 1.13949E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 1.54927E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 1.88891E-06;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 1.35444E-06;
                        else contact_prob = 0.0; break;                
                    case 1:
                        if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 0.000000815;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 2.38073E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 4.26982E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 5.80531E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 7.07799E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 5.07526E-07;
                        else contact_prob = 0.0; break;
                    case 0:
                        if(scenario_num_now == 4 || (scenario_num_now == 5 && accepted_range_now == 1.0)) contact_prob = 2.725E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.2) contact_prob = 7.96011E-08;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.4) contact_prob = 1.42764E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.6) contact_prob = 1.94104E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.8) contact_prob = 2.36657E-07;
                        else if(scenario_num_now == 5 && accepted_range_now == 0.5) contact_prob = 1.69694E-07;
                        else contact_prob = 0.0; break;
                    default: System.out.println("there is a type error: g_type 8(neighborhood) in disease class");
                }
                break;
            case 10: // Bed
                break;
            case 14: // publicly used facilities
                contact_prob = 0.21;
                break;
            case 17: // Bubble
                if(i_type == 0 || i_type == 1) //infectious = child
                    {
                        if(s_type == 0 || s_type == 1){
                            if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 1.321;
                            else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.77;
                            else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.544;
                            else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.42;
                            else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.342;
                        }
                        else if( s_type == 2 || s_type == 3 || s_type == 4 ){
                            if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.578;
                            else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.337;
                            else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.238;
                            else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.184;
                            else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.15;
                        }
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                        if(s_type == 0 || s_type == 1){
                            if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.413;
                            else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.241;
                            else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.17;
                            else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.131;
                            else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.107;
                        }
                        else if(s_type == 2 || s_type == 3 || s_type == 4 ){
                            if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.66;
                            else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.385;
                            else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.272;
                            else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.21;
                            else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.171;
                        }
                        else System.out.println("there is a type error: g_type 0(household) in disease class");
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                    break;
            case 18: // Bubble
                if(i_type == 0 || i_type == 1) //infectious = child
                {
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 1.321;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.77;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.544;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.42;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.342;
                    }
                    else if( s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.578;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.337;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.238;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.184;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.15;
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.413;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.241;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.17;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.131;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.107;
                    }
                    else if(s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.66;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.385;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.272;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.21;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.171;
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else System.out.println("there is a type error: g_type 0(household) in disease class");
                break;
            case 19: // Bubble
                if(i_type == 0 || i_type == 1) //infectious = child
                {
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 1.321;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.77;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.544;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.42;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.342;
                    }
                    else if( s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.578;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.337;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.238;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.184;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.15;
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else if(i_type == 2 || i_type == 3 || i_type == 4 ){
                    if(s_type == 0 || s_type == 1){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.413;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.241;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.17;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.131;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.107;
                    }
                    else if(s_type == 2 || s_type == 3 || s_type == 4 ){
                        if(scenario_num_now == 2 && bubble_size_now == 1) contact_prob = 0.66;
                        else if(scenario_num_now == 2 && bubble_size_now == 2) contact_prob = 0.385;
                        else if(scenario_num_now == 2 && bubble_size_now == 3) contact_prob = 0.272;
                        else if(scenario_num_now == 2 && bubble_size_now == 4) contact_prob = 0.21;
                        else if(scenario_num_now == 2 && bubble_size_now == 5) contact_prob = 0.171;
                    }
                    else System.out.println("there is a type error: g_type 0(household) in disease class");
                }
                else System.out.println("there is a type error: g_type 0(household) in disease class");
                break;
            default:
                System.out.println("there is a type error: group type in disease model");
        }
        return contact_prob;
    }
}
