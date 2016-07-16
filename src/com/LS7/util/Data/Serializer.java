package com.LS7.util.Data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Serializer {

    public static interface Serializable<T>{
		public Obj getSerialized();
		public T deserialize(Obj o);
    }

    public static String HEADER="LS7DefaultSerializedFile";
    public static short VERSION=0x0001;

    public byte[] Objects;
    public byte[] footer;

    // TODO: Types
    public static class Type{
            public static final byte UNKNOWN = 0;
            public static final byte BYTE    = 1;
            public static final byte SHORT   = 2;
            public static final byte CHAR    = 3;
            public static final byte INTEGER = 4;
            public static final byte FLOAT   = 5;
            public static final byte LONG    = 6;
            public static final byte DOUBLE  = 7;
            public static final byte BOOLEAN = 8;
            public static final byte STRING  = 9;
            public static int getSize(byte t){
                    if(t==BYTE)   return Byte.BYTES;
                    if(t==SHORT)  return Short.BYTES;
                    if(t==CHAR)   return Character.BYTES;
                    if(t==INTEGER)return Integer.BYTES;
                    if(t==FLOAT)  return Float.BYTES;
                    if(t==LONG)   return Long.BYTES;
                    if(t==DOUBLE) return Double.BYTES;
                    if(t==BOOLEAN)return 1;
                    if(t==STRING) return Character.BYTES;
                    return -1;
            }
    }
    public static class ContainerType{
            public static final byte UNKNOWN  = 0;
            public static final byte FIELD    = 1;
            public static final byte ARRAY    = 2;
            public static final byte OBJECT   = 3;
            public static final byte DATABASE = 4;
    }
    // TODO: DataBase
    public static class Database{
            public static final byte CONTAINER_TYPE=ContainerType.DATABASE;
            public short nameLength;
            public byte[] name;
            public int size=Type.getSize(Type.BYTE)+Type.getSize(Type.SHORT)*2+Type.getSize(Type.INTEGER);
            private short objCount;
            private final List<Obj> objects= new ArrayList<Obj>();
            public Database(String name){
                    setName(name);
            }
            public void addObj(Obj f){
                objects.add(f);
                //size+=f.getSize();
                objCount++;
            }
            public void addObj(Obj[] f){
            	for(Obj o : f)
            		addObj(o);
            }
            public final void setName(String name){
                    assert(name.length()<Short.MAX_VALUE);
                    nameLength=(short)name.length();
                    this.name=name.getBytes();
            }
            public String getName(){
                    return new String(name);
            }
            public Obj getObj(String name){
                    for(int n=0;n<getObjCount();n++){
                            if(new String(getObj(n).name).equals(name)){
                                    return getObj(n);
                            }
                    }
                    return null;
            }
            public Obj getObj(int i){
                    return objects.get(i);
            }
            public int getObjCount(){
                    return objects.size();
            }
            /*public int getSize(){
                int s = size;
                    for(Obj o:objects){
                        s+=o.getSize();
                    }
                    s+=name.length;
                    return s;
            }*/
            public int getBytes(byte[] dest, int pointer){
                    pointer = rw.writeBytes(dest,pointer,HEADER.getBytes());
                    pointer = rw.writeBytes(dest,pointer,CONTAINER_TYPE);
                    pointer = rw.writeBytes(dest,pointer,VERSION);
                    pointer = rw.writeBytes(dest,pointer,nameLength);
                    pointer = rw.writeBytes(dest,pointer,name);
                    pointer = rw.writeBytes(dest,pointer,size);
                    pointer = rw.writeBytes(dest,pointer,objCount);
                    for(Obj o:objects)
                            pointer = o.getBytes(dest, pointer);
                    return pointer;
            }
            public byte[] getBytes(){
                    byte[] dest = new byte[getSize()];
                    int pointer = rw.writeBytes(dest,0,HEADER.getBytes());
                    pointer = rw.writeBytes(dest,pointer,CONTAINER_TYPE);
                    pointer = rw.writeBytes(dest,pointer,VERSION);
                    pointer = rw.writeBytes(dest,pointer,nameLength);
                    pointer = rw.writeBytes(dest,pointer,name);
                    pointer = rw.writeBytes(dest,pointer,size);
                    pointer = rw.writeBytes(dest,pointer,objCount);
                    for(Obj o:objects)
                            pointer = o.getBytes(dest, pointer);
                    return dest;
            }
            public int getSize(){
                int s = size+HEADER.length()+name.length;
                for(Obj o:objects)
                    s+=o.getSize();
                return s;
            }
            public static Database deserialize(byte[] data) {
                    Database db = new Database("");
                    int pointer =0;
                    String fh = rw.readString(data,pointer,HEADER.length());
                    if(!fh.equals(HEADER)){
                            System.err.println("Seralizer Error: Wrong header(Database)");
                            System.err.println("\tFound:"+fh+", Expected:"+HEADER+".");
                            return null;
                    }
                    pointer+=HEADER.length();

                    byte ct = rw.readByte(data, pointer);
                    if(ct!=CONTAINER_TYPE){
                            System.err.println("Seralizer Error: Wrong container Type(Database)");
                            System.err.println("\tFound:"+ct+", Expected:"+CONTAINER_TYPE+".");
                            return null;
                    }
                    pointer+=Type.getSize(Type.BYTE);

                    short ver = rw.readShort(data, pointer);
                    if(ver!=VERSION){
                        System.err.println("Seralizer Error: Wrong Version");
                        System.err.println("\tFound:"+ver+", Expected:"+VERSION+".");
                        return null;
                    }
                    pointer+=Type.getSize(Type.SHORT);
                    
                    db.nameLength = rw.readShort(data, pointer);
                    pointer+=Type.getSize(Type.SHORT);

                    db.name = rw.readString(data, pointer, db.nameLength).getBytes();
                    pointer+=db.nameLength;

                    db.size = rw.readInt(data, pointer);
                    pointer+=Type.getSize(Type.INTEGER);

                    db.objCount = rw.readShort(data, pointer);
                    pointer+=Type.getSize(Type.SHORT);

                    for(int i=0;i<db.objCount;i++){
                            Obj obj = Obj.deserialize(data, pointer);
                            db.objects.add(obj);
                            pointer+=obj.size;
                    }

                    return db;
            }
    }
    // TODO: Obj
    public static class Obj{
            public static final byte CONTAINER_TYPE=ContainerType.OBJECT;
            public short nameLength;
            public byte[] name;
            public int size = Type.getSize(Type.BYTE)+Type.getSize(Type.SHORT)*3+Type.getSize(Type.INTEGER);
            private short fieldCount;
            private short arrayCount;
            private final List<Field> fields= new ArrayList<Field>();
            private final List<Array> arrays= new ArrayList<Array>();

            public Obj(String name){
                    setName(name);
            }

            private Obj(){}

            public void addField(Field f){
                    fields.add(f);
                    //size+=f.getSize();
                    fieldCount++;
            }
            public void addArray(Array a){
                    arrays.add(a);
                    //size+=a.getSize();
                    arrayCount++;
            }
            public Field getField(int i){
                    return fields.get(i);
            }
            public Field getField(String name){
                for(int i=0;i<fields.size();i++){
                	if(fields.get(i).name.equals(name))
                		return fields.get(i);
                }
                return null;
            }
            public Array getArray(int i){
                    return arrays.get(i);
            }
            public Array getArray(String name){
                for(int i=0;i<arrays.size();i++){
                	if(arrays.get(i).name.equals(name))
                		return arrays.get(i);
                }
                return null;
            }
            public int getBytes(byte[] dest, int pointer){
                    pointer = rw.writeBytes(dest,pointer,CONTAINER_TYPE);
                    pointer = rw.writeBytes(dest,pointer,nameLength);
                    pointer = rw.writeBytes(dest,pointer,name);
                    pointer = rw.writeBytes(dest,pointer,size);
                    pointer = rw.writeBytes(dest,pointer,fieldCount);
                    pointer = rw.writeBytes(dest,pointer,arrayCount);
                    for(Field f:fields)
                            pointer = f.getBytes(dest, pointer);
                    for(Array a:arrays)
                            pointer = a.getBytes(dest, pointer);
                    return pointer;
            }
            public int getSize(){
                int s=size;
                    for(Field f:fields)
                        s+=f.getSize();
                    for(Array a:arrays)
                        s+=a.getSize();
                s+=nameLength;
                return s;
            }
            public final void setName(String name){
                    assert(name.length()<Short.MAX_VALUE);
                    /*if(this.name!=null)
                            size-=this.name.length;*/
                    nameLength=(short)name.length();
                    this.name=name.getBytes();
                    //size+=nameLength;
            }
            public static Obj deserialize(byte[] data, int pointer) {
                    Obj obj = new Obj();
                    int ct = rw.readByte(data, pointer);
                    if(ct!=CONTAINER_TYPE){
                            System.err.println("Seralizer Error: Wrong container Type(Obj)");
                            return null;
                    }
                    pointer+=Type.getSize(Type.BYTE);

                    obj.nameLength = rw.readShort(data, pointer);
                    pointer+=Type.getSize(Type.SHORT);

                    obj.name = rw.readString(data, pointer, obj.nameLength).getBytes();
                    pointer+=obj.nameLength;

                    obj.size = rw.readInt(data, pointer);
                    pointer+=Type.getSize(Type.INTEGER);

                    obj.fieldCount = rw.readShort(data, pointer);
                    pointer+=Type.getSize(Type.SHORT);

                    obj.arrayCount = rw.readShort(data, pointer);
                    pointer+=Type.getSize(Type.SHORT);

                    for(int i=0;i<obj.fieldCount;i++){
                            Field field = Field.deserialize(data, pointer);
                            obj.fields.add(field);
                            pointer+=field.size;
                    }
                    for(int i=0;i<obj.arrayCount;i++){
                            Array array = Array.deserialize(data, pointer);
                            obj.arrays.add(array);
                            pointer+=array.size;
                    }

                    return obj;
            }

    }
    // TODO: Array
    public static class Array{
            public static final byte CONTAINER_TYPE=ContainerType.ARRAY;
            public short nameLength;
            public byte[] name;
            public int size = Type.getSize(Type.BYTE)*2+Type.getSize(Type.SHORT)+Type.getSize(Type.INTEGER)*2;
            public byte type;
            public int count;
            public byte[] byteData;
            public short[] shortData;
            public char[] charData;
            public int[] integerData;
            public float[] floatData;
            public long[] longData;
            public double[] doubleData;
            public boolean[] booleanData;

            private Array(){};
            public void setName(String name){
                    assert(name.length()<Short.MAX_VALUE);
                    //if(this.name!=null)
                    //	size-=this.name.length;
                    nameLength=(short)name.length();
                    this.name=name.getBytes();
                    //size+=nameLength;
            }
            public int getBytes(byte[] dest, int pointer){
                count = getDataSize();
                pointer = rw.writeBytes(dest,pointer,CONTAINER_TYPE);
                pointer = rw.writeBytes(dest,pointer,nameLength);
                pointer = rw.writeBytes(dest,pointer,name);
                pointer = rw.writeBytes(dest,pointer,type);
                pointer = rw.writeBytes(dest,pointer,size);
                pointer = rw.writeBytes(dest,pointer,count);
                switch(type){
                case Type.BYTE:
                        pointer = rw.writeBytes(dest,pointer,byteData);
                        break;
                case Type.SHORT:
                        pointer = rw.writeBytes(dest,pointer,shortData);
                        break;
                case Type.CHAR:
                        pointer = rw.writeBytes(dest,pointer,charData);
                        break;
                case Type.INTEGER:
                        pointer = rw.writeBytes(dest,pointer,integerData);
                        break;
                case Type.FLOAT:
                        pointer = rw.writeBytes(dest,pointer,floatData);
                        break;
                case Type.LONG:
                        pointer = rw.writeBytes(dest,pointer,longData);
                        break;
                case Type.DOUBLE:
                        pointer = rw.writeBytes(dest,pointer,doubleData);
                        break;
                case Type.BOOLEAN:
                        pointer = rw.writeBytes(dest,pointer,booleanData);
                        break;
                case Type.STRING:
                        pointer = rw.writeBytes(dest,pointer,charData);
                        break;
                }
                return pointer;
            }
            public int getSize(){
                    //assert(data.length==Type.getSize(type));
                    return size+getDataSize()+nameLength;//Type.getSize(Type.BYTE)+Type.getSize(Type.SHORT)+name.length+Type.getSize(Type.BYTE)+Type.getSize(Type.INTEGER)+getDataSize()*Type.getSize(type);
            }
            public int getDataSize(){
                    switch(type){
                    case Type.BYTE:
                            return(byteData.length)*Type.getSize(type);
                    case Type.SHORT:
                            return(shortData.length)*Type.getSize(type);
                    case Type.CHAR:
                            return(charData.length)*Type.getSize(type);
                    case Type.INTEGER:
                            return(integerData.length)*Type.getSize(type);
                    case Type.FLOAT:
                            return(floatData.length)*Type.getSize(type);
                    case Type.LONG:
                            return(longData.length)*Type.getSize(type);
                    case Type.DOUBLE:
                            return(doubleData.length)*Type.getSize(type);
                    case Type.BOOLEAN:
                            return(booleanData.length)*Type.getSize(type);
                    case Type.STRING:
                            return(charData.length)*Type.getSize(type);
                    }
                    return 0;
            }
            // TODO: Array creators
            public static Array deserialize(byte[] data, int pointer){
                    Array a = new Array();
                    int ct = rw.readByte(data, pointer);
                    if(ct!=CONTAINER_TYPE){
                            System.err.println("Seralizer Error: Wrong container Type(Array)");
                            return null;
                    }
                    pointer+=Type.getSize(Type.BYTE);

                    a.nameLength = rw.readShort(data, pointer);
                    pointer+=Type.getSize(Type.SHORT);

                    a.name = rw.readString(data, pointer, a.nameLength).getBytes();
                    pointer+=a.nameLength;

                    a.type = rw.readByte(data, pointer);
                    pointer+=Type.getSize(Type.BYTE);

                    a.size = rw.readInt(data, pointer);
                    pointer+=Type.getSize(Type.INTEGER);

                    /*System.out.println(rw.readByte(data, pointer));
                    System.out.println(rw.readByte(data, pointer+1));
                    System.out.println(rw.readByte(data, pointer+2));
                    System.out.println(rw.readByte(data, pointer+3));
                    System.out.println(rw.readInt(data, pointer));*/
                    a.count = rw.readInt(data, pointer);
                    pointer+=Type.getSize(Type.INTEGER);

                    switch(a.type){
                    case Type.BYTE:
                            a.byteData=new byte[a.count];
                            rw.readBytes(data,pointer,a.byteData);
                            break;
                    case Type.SHORT:
                            a.byteData=new byte[a.count];
                            rw.readShort(data,pointer,a.shortData);
                            break;
                    case Type.CHAR:
                            a.charData=new char[a.count];
                            rw.readChar(data,pointer,a.charData);
                            break;
                    case Type.INTEGER:
                            a.integerData=new int[a.count];
                            rw.readInt(data,pointer,a.integerData);
                            break;
                    case Type.FLOAT:
                            a.floatData=new float[a.count];
                            rw.readFloat(data,pointer,a.floatData);
                            break;
                    case Type.LONG:
                            a.longData=new long[a.count];
                            rw.readLong(data,pointer,a.longData);
                            break;
                    case Type.DOUBLE:
                            a.doubleData=new double[a.count];
                            rw.readDouble(data,pointer,a.doubleData);
                            break;
                    case Type.BOOLEAN:
                            a.booleanData=new boolean[a.count];
                            rw.readBoolean(data,pointer,a.booleanData);
                            break;
                    case Type.STRING:
                            a.charData=new char[a.count];
                            rw.readChar(data,pointer,a.charData);
                            break;
                    }
                    return a;
            }
            public static Array byteArray(String name, byte[] data){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.BYTE;
                    a.count=data.length;
                    a.byteData = data;
                    return a;
            }
            public static Array shortArray(String name, short[] d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.SHORT;
                    a.count=d.length;
                    a.shortData = d;
                    return a;
            }
            public static Array charArray(String name, char[] d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.CHAR;
                    a.count=d.length;
                    a.charData = d;
                    return a;
            }
            public static Array intArray(String name, int[] d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.INTEGER;
                    a.count=d.length;
                    a.integerData = d.clone();
                    return a;
            }
            public static Array floatArray(String name, float[] d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.FLOAT;
                    a.count=d.length;
                    a.floatData = d;
                    return a;
            }
            public static Array longArray(String name, long[] d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.LONG;
                    a.count=d.length;
                    a.longData = d;
                    return a;
            }
            public static Array doubleArray(String name, double[] d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.DOUBLE;
                    a.count=d.length;
                    a.doubleData = d;
                    return a;
            }
            public static Array doubleArray(String name, boolean[] d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.BOOLEAN;
                    a.count=d.length;
                    a.booleanData = d;
                    return a;
            }
            public static Array stringArray(String name, String d){
                    Array a = new Array();
                    a.setName(name);
                    a.type = Type.STRING;
                    a.count=d.length();
                    a.charData = d.toCharArray();
                    return a;
            }

    }
    // TODO: Field
    public static class Field{
            public static final byte CONTAINER_TYPE=ContainerType.FIELD;
            public short nameLength;
            public byte[] name;
            public byte type;
            int size = Type.getSize(Type.BYTE)+Type.getSize(Type.SHORT)+Type.getSize(Type.BYTE);
            public byte[] data;
            private Field(){}
            public void setName(String name){
                    assert(name.length()<Short.MAX_VALUE);
                    nameLength=(short)name.length();
                    this.name=name.getBytes();
            }
            public Object getData(){
            	switch(type){
            	case Type.BYTE:
            		return data[0];
            	case Type.SHORT:
            		return rw.readShort(data, 0);
            	case Type.BOOLEAN:
            		return rw.readBoolean(data, 0);
            	case Type.CHAR:
            		return rw.readChar(data, 0);
            	case Type.INTEGER:
            		return rw.readInt(data, 0);
            	case Type.FLOAT:
            		return rw.readFloat(data, 0);
            	case Type.LONG:
            		return rw.readLong(data, 0);
            	case Type.DOUBLE:
            		return rw.readDouble(data, 0);
            	}
            	return null;
            }
            public int getBytes(byte[] dest, int pointer){
                    pointer = rw.writeBytes(dest,pointer,CONTAINER_TYPE);
                    pointer = rw.writeBytes(dest,pointer,nameLength);
                    pointer = rw.writeBytes(dest,pointer,name);
                    pointer = rw.writeBytes(dest,pointer,type);
                    pointer = rw.writeBytes(dest,pointer,data);
                    return pointer;
            }
            public int getSize(){
                    assert(data.length==Type.getSize(type));
                    return Type.getSize(Type.BYTE)+Type.getSize(Type.SHORT)+Type.getSize(Type.BYTE)+data.length+name.length;
            }

            // TODO: Field creators
            public static Field deserialize(byte[] data, int pointer){
                    Field f = new Field();
                    int ct = rw.readByte(data, pointer);
                    if(ct!=CONTAINER_TYPE){
                            System.err.println("Seralizer Error: Wrong container Type(field)");
                            System.err.println("\tFound:"+ct+", Expected:"+CONTAINER_TYPE+".");
                            System.err.println("\tpointer:"+pointer);
                            return null;
                    }
                    pointer+=Type.getSize(Type.BYTE);

                    f.nameLength = rw.readShort(data, pointer);
                    pointer+=Type.getSize(Type.SHORT);

                    f.name = rw.readString(data, pointer, f.nameLength).getBytes();
                    pointer+=f.nameLength;

                    f.type = rw.readByte(data, pointer);
                    pointer+=Type.getSize(Type.BYTE);

                    f.data=new byte[Type.getSize(f.type)];
                    for(int i=0;i<f.data.length;i++){
                            f.data[i]=rw.readByte(data, pointer);
                    }
                    pointer+=f.data.length;
                    f.size = Type.getSize(Type.BYTE)*(2+Type.getSize(f.type))+Type.getSize(Type.SHORT)+f.nameLength;
                    return f;
            }
            public static Field byteField(String name, byte value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.BYTE;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
            public  static Field shortField(String name, short value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.SHORT;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
            public  static Field charField(String name, char value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.CHAR;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
            public  static Field intField(String name, int value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.INTEGER;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
            public  static Field floatField(String name, float value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.FLOAT;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
            public  static Field longField(String name, long value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.LONG;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
            public  static Field doubleField(String name, double value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.DOUBLE;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
            public  static Field booleanField(String name, boolean value){
                    Field f = new Field();
                    f.setName(name);
                    f.type = Type.BOOLEAN;
                    f.data = new byte[Type.getSize(f.type)];
                    rw.writeBytes(f.data,0,value);
                    return f;
            }
    }

    public static class rw{
            // TODO: Writing
            public static int writeBytes(byte[] dest, int pointer, byte[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.BYTE)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, short[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.SHORT)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, char[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.CHAR)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, int[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.INTEGER)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, long[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.LONG)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, float[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.FLOAT)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, double[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.DOUBLE)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, boolean[] value){
                    assert(dest.length>pointer+(Type.getSize(Type.BOOLEAN)*value.length));
                    for(int i=0;i<value.length;i++)
                            pointer = writeBytes(dest,pointer,value[i]);
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, byte value){
                    assert(dest.length>pointer+Type.getSize(Type.BYTE));
                    dest[pointer++]=value;
                    return pointer;
            }
            public static int writeBytes(byte[] dest, int pointer, short value){
                    assert(dest.length>pointer+Type.getSize(Type.SHORT));
                    pointer = writeBytes(dest,pointer,(byte)((value>>8)&0xff));
                    return writeBytes(dest,pointer,(byte)((value)&0xff));
            }
            public static int writeBytes(byte[] dest, int pointer, char value){
                    assert(dest.length>pointer+Type.getSize(Type.CHAR));
                    pointer = writeBytes(dest,pointer,(byte)((value>>8)&0xff));
                    return writeBytes(dest,pointer,(byte)((value)&0xff));
            }
            public static int writeBytes(byte[] dest, int pointer, int value){
                    assert(dest.length>pointer+Type.getSize(Type.INTEGER));
                    pointer = writeBytes(dest,pointer,(byte)((value>>24)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>16)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>8)&0xff));
                    return writeBytes(dest,pointer,(byte)((value)&0xff));
            }
            public static int writeBytes(byte[] dest, int pointer, long value){
                    assert(dest.length>pointer+Type.getSize(Type.LONG));
                    pointer = writeBytes(dest,pointer,(byte)((value>>56)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>48)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>40)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>32)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>24)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>16)&0xff));
                    pointer = writeBytes(dest,pointer,(byte)((value>>8)&0xff));
                    return writeBytes(dest,pointer,(byte)((value)&0xff));
            }
            public static int writeBytes(byte[] dest, int pointer, float value){
                    assert(dest.length>pointer+Type.getSize(Type.FLOAT));
                    int r = Float.floatToIntBits(value);
                    return writeBytes(dest,pointer,r);
            }
            public static int writeBytes(byte[] dest, int pointer, double value){
                    assert(dest.length>pointer+Type.getSize(Type.DOUBLE));
                    long r = Double.doubleToLongBits(value);
                    return writeBytes(dest,pointer,r);
            }
            public static int writeBytes(byte[] dest, int pointer, boolean value){
                    assert(dest.length>pointer+Type.getSize(Type.BOOLEAN));
                    return writeBytes(dest,pointer,(byte)(value?1:0));
            }
            public static int writeBytes(byte[] dest, int pointer, String value){
                    pointer = writeBytes(dest,pointer,(short)(value.length()));
                    return writeBytes(dest,pointer,value.getBytes());
            }
            // TODO: Reading
            public static byte readByte(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.BYTE));
                    return src[pointer];
            }
            public static short readShort(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.SHORT));
                    return (short)((src[pointer+0]<<8)|src[pointer+1]);
            }
            public static char readChar(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.CHAR));
                    return (char)((src[pointer+0]<<8)|src[pointer+1]);
            }
            public static int readInt(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.INTEGER));
                    int v1 = (int)((src[pointer]<<24)& 0xFF);
                    int v2 = (int)((src[pointer+1]<<16)& 0xFF);
                    int v3 = (int)((src[pointer+2]<<8)& 0xFF);
                    int v4 = (int)((src[pointer+3])& 0xFF);
                    return v1|v2|v3|v4;
            }
            public static long readLong(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.LONG));
                    return (long)((src[pointer]<<56)|(src[pointer+1]<<48)|(src[pointer+2]<<40)|(src[pointer+3]<<32)|(src[pointer+4]<<24)|(src[pointer+5]<<16)|(src[pointer+6]<<8)|src[pointer+7]);
            }
            public static float readFloat(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.FLOAT));
                    return Float.intBitsToFloat(readInt(src,pointer));
            }
            public static double readDouble(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.DOUBLE));
                    return Double.longBitsToDouble(readLong(src,pointer));
            }
            public static boolean readBoolean(byte[] src, int pointer){
                    assert(src.length>pointer+Type.getSize(Type.BOOLEAN));
                    assert(src[pointer]==0||src[pointer]==1);
                    return src[pointer]!=0;
            }
            public static String readString(byte[] src, int pointer, int length){
                    assert(src.length>pointer+length*Type.getSize(Type.STRING));
                    return new String(src, pointer, length);
            }
            public static void readBytes(byte[] data, int pointer, byte[] byteData) {
                    for(int i=0;i<byteData.length;i++)
                            byteData[i]=data[pointer+i];
            }
            public static void readShort(byte[] data, int pointer, short[] shortData) {
                    for(int i=0;i<shortData.length;i++)
                            shortData[i]=readShort(data, pointer+i*Type.getSize(Type.SHORT));
            }
            public static void readChar(byte[] data, int pointer, char[] charData) {
                    for(int i=0;i<charData.length;i++)
                            charData[i]=readChar(data, pointer+i*Type.getSize(Type.CHAR));
            }
            public static void readInt(byte[] data, int pointer, int[] intData) {
                    for(int i=0;i<intData.length;i++)
                            intData[i]=readInt(data, pointer+i*Type.getSize(Type.INTEGER));
            }
            public static void readFloat(byte[] data, int pointer, float[] floatData) {
                    for(int i=0;i<floatData.length;i++)
                            floatData[i]=readFloat(data, pointer+i*Type.getSize(Type.FLOAT));
            }
            public static void readLong(byte[] data, int pointer, long[] longData) {
                    for(int i=0;i<longData.length;i++)
                            longData[i]=readInt(data, pointer+i*Type.getSize(Type.LONG));
            }
            public static void readDouble(byte[] data, int pointer, double[] doubleData) {
                    for(int i=0;i<doubleData.length;i++)
                            doubleData[i]=readFloat(data, pointer+i*Type.getSize(Type.DOUBLE));
            }
            public static void readBoolean(byte[] data, int pointer, boolean[] booleanData) {
                    for(int i=0;i<booleanData.length;i++)
                            booleanData[i]=readBoolean(data, pointer+i*Type.getSize(Type.BOOLEAN));
            }
    }

    public static void saveToFile(String path, byte[] data){
        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
            stream.write(data);
            stream.close();
        } catch (Exception e) {
                Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public static byte[] loadFromFile(String path){
            byte[] b=null;
            try {
                    BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path));
                    b = new byte[stream.available()];
                    stream.read(b);
                    stream.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            return b;
    }

    public static void serializationTest(){
        String c = "A really Full Inventory and some random words to"
                        + "make this string really really long :D";
        Array inv = Array.stringArray("Inventory", c);
        Field hp = Field.byteField("Health", (byte) 100);
        Field mp = Field.byteField("Mana", (byte) 100);
        Field xpos = Field.byteField("xPos", (byte) 25);
        Field ypos = Field.byteField("yPos", (byte) 10);
        Field map = Field.byteField("mapID", (byte) 2);

        Database db= new Database("Really cool database");

        Obj player = new Obj("Player");
        player.addArray(inv);
        player.addField(hp);
        player.addField(mp);
        player.addField(xpos);
        player.addField(ypos);
        player.addField(map);

        db.addObj(player);

        byte[] res = new byte[db.getSize()];
        db.getBytes(res, 0);
        saveToFile("db.save",res);
    }
    public static void deserializationTest(){
            Database.deserialize(loadFromFile("db.save"));
    }
    /*public static void main(String[] args){
            serializationTest();
            deserializationTest();
    }*/

}



