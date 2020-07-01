package com.github.model;

public class Enumeration {
    public enum VehicleType{
        TRAIN, ISOMETRIC_TRAIN,
        REGION_BUS, ISOMETRIC_REGION_BUS,
        CITY_BUS,ISOMETRIC_CITY_BUS,
        TAXI;

        public static int getVehicleSpeed(VehicleType type){
            switch (type){
                case TRAIN:
                    return 130;
                case ISOMETRIC_TRAIN:
                    break;
                case REGION_BUS:
                    return 110;
                case ISOMETRIC_REGION_BUS:
                    break;
                case CITY_BUS:
                    return 70;
                case ISOMETRIC_CITY_BUS:
                    break;
                case TAXI:
                    break;

            }
            return 0;
        }
    }
    public enum VehicleSituation{
            OUT_OF_SERVICE,
            STOP_SWITCH_DESTINATION,
            STOP,
            RUN
    }
    public enum Station{
        NORTH, VALLE_OF_ARRYN, CROWNLAND, REACH, STORMLANDS, DORNE, ROCKLAND,
        DAENERYS_TARGARYEN_A, DAENERYS_TARGARYEN_B, DAENERYS_TARGARYEN_C, DAENERYS_TARGARYEN_D,
        TYRION_LANNISTER_A, TYRION_LANNISTER_B, TYRION_LANNISTER_C, TYRION_LANNISTER_D,
        JON_SNOW_A, JON_SNOW_B, JON_SNOW_C, JON_SNOW_D, JON_SNOW_E,JON_SNOW_F, JON_SNOW_G


        }


}
