package server.cheat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import shared.IDice;

public class CheatEnabledDice implements IDice {
	
	private Random rand;
	private Deque<Integer> rollQueue;
	
	public CheatEnabledDice() {
		rand = new Random();
		rollQueue = new ArrayDeque<>();
	}
	
	public void enqueueRoll(int roll) {
		rollQueue.addLast(roll);
	}
	
	public List<Integer> seeNext(int amount) {
		if (rollQueue.size() > amount) {
			return new ArrayList<>(rollQueue).subList(0, amount);
		}
		
		while (rollQueue.size() < amount) {
			enqueueRoll(rand.nextInt(6) + rand.nextInt(6) + 2);
		}
		
		return new ArrayList<>(rollQueue);
	}

	@Override
	public int roll() {
		if (rollQueue.isEmpty()) {
			return rand.nextInt(6) + rand.nextInt(6) + 2;
		}
		else {
			return rollQueue.removeFirst();
		}
	}

	@Override
	public int roll(int num) {
		// TODO Auto-generated method stub
		return num;
	}

}