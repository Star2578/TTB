package utils;

public interface BaseStatus {
    int getCurrentHealth();
    void setCurrentHealth(int health);

    int getMaxHealth();
    void setMaxHealth(int maxHealth);

    boolean isAlive();
    void onDeath();
}
