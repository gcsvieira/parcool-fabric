package com.alrex.parcool.client.animation;

import java.util.LinkedList;
import java.util.List;

public class AnimatorList {
    private final List<Animator> animators = new LinkedList<>();

    public void add(Animator animator) {
        animators.add(animator);
    }

    public void remove(Animator animator) {
        animators.remove(animator);
    }

    public boolean isEmpty() {
        return animators.isEmpty();
    }

    public List<Animator> getList() {
        return animators;
    }
}
