package edu.rutgers.winlab.sim.test;



public class TestOO {
	private int value;
	
	public class MyTestNonStaticClass {
		public void setValue(int newValue) {
			value = newValue;
		}
	}
	
	
	public static abstract class TestAbstract {
		protected int val;
		public abstract void setVal(int newVal);
		public void printVal() {
			System.out.printf("val=%d%n", val);
		}
	}

	public static class TestAbstract1 extends TestAbstract {

		@Override
		public void setVal(int newVal) {
			val = newVal + 3;
		}
	}

	public static class TestAbstract2 extends TestAbstract {

		@Override
		public void setVal(int newVal) {
			val = newVal * 5;
			
		}
	}
	
	public static abstract class TestFurtherAbstract extends TestAbstract {
		protected int val2;
		public void extendedFunction() {
			
		}
		public abstract void extendedAbstract();
	}
	
	public static class ChildClass extends TestFurtherAbstract {

		@Override
		public void extendedAbstract() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setVal(int newVal) {
			// TODO Auto-generated method stub
			
		}
		
	}


	public static void main(String[] args) {
		TestAbstract[] testAbstracts = new TestAbstract[3];
		testAbstracts[0] = new TestAbstract1();
		testAbstracts[1] = new TestAbstract2();
		testAbstracts[2] = new TestAbstract1();
		
		for (TestAbstract ta : testAbstracts) {
			ta.setVal(5);
			ta.printVal();
		}
	}

}
