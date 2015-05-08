package application.utils;

public class ObjectCollection {

    public Object[] objects;

    public ObjectCollection(int numberOfElements){
        objects = new Object[numberOfElements];
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectCollection)) {
            return false;
        }

        ObjectCollection object = (ObjectCollection) obj;

        int i = 0;
        for(Object o : object.objects){
            if(!objects[i++].equals(o)){
                return false;
            }
        }

        return  true;


    }

    @Override
    public int hashCode() {
        int hash = 1;
        for(Object o : objects){
            hash += o.hashCode();
        }
        return hash;
    }


}
