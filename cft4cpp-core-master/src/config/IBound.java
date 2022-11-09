package config;

public interface IBound {
    Object clone() throws CloneNotSupportedException;

    int getLower();

    void setLower(int lower);

    int getUpper();

    void setUpper(int upper);

}