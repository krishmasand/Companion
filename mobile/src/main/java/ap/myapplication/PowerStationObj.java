package ap.myapplication;

/**
 * Created by Ankit on 1/11/2015.
 */
public class PowerStationObj {

//        public String id;
        public String address;
        public float[] location;

        public PowerStationObj(){
            super();
        }

        public PowerStationObj(String address, float[] location) {
            super();
            this.address = address;
            this.location = location;
        }

        @Override
        public String toString() {
            return this.address;
        }
    }


