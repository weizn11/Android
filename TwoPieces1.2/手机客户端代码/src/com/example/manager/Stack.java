package com.example.manager;

import java.util.ArrayList;

public class Stack<E> {
	private ArrayList<E> objs;
	private int index;
	public int length;

	public Stack() {
		objs = new ArrayList<E>();
		length = 0;
		index = -1;
	}

	public void initialize() {
		objs.clear();
		length = 0;
		index = -1;
	}

	public void push(E obj) {// 入栈
		this.objs.add(obj);
		index++;
		length++;
	}

	public E pop() {// 出栈
		if (index > -1) {
			E obj = this.objs.get(index);
			this.objs.remove(index);
			index--;
			length--;
			return obj;
		}
		return null;
	}

	public E GetTop() {// 返回栈顶的元素
		if (index > -1) {
			E obj = this.objs.get(index);
			return obj;
		}
		return null;
	}
}
