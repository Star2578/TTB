package skills;

import pieces.BasePiece;

public interface SingleTargetSkill<Type extends BasePiece> {
    void setTarget(Type target);
}
