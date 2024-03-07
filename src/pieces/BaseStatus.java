package pieces;

public interface BaseStatus {
    int getCurrentHealth();
    void setCurrentHealth(int health);

    int getMaxHealth();
    void setMaxHealth(int maxHealth);

    void takeDamage(int damage);

    boolean isAlive();
    void onDeath();
}
