
//terkel and jos hw1

import java.util.Scanner;
import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//added test
//
/*
* Hints in doing the HW:
*   a) Make sure you first understand what you are doing.
*   b) Watch Lecture 2 focusing on the code described
 */


class homework2 {
	private static boolean debug =false;
    // Abstract Syntax Tree
    static final class AST {
        public final String value;
        public final AST left; // can be null
        public final AST right; // can be null

        private AST(String val,AST left, AST right) {
            value = val;
            this.left = left;
            this.right = right;
        }

        public static AST createAST(Scanner input) {
            if (!input.hasNext())
                return null;

            String value = input.nextLine();
            if (value.equals("~"))
                return null;

            return new AST(value, createAST(input), createAST(input));
        }
    }

	static public class Array_info{
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Array_info [type=" + type + ", typeSize=" + typeSize + ", dim=" + dim + ", totalSize=" + totalSize
					+ ", subpart=" + subpart + ", l=" + l + ", ixa=" + Arrays.toString(ixa) + "]";
		}


		public String type;
    	public int typeSize;                      // typeSize
    	public ArrayList<Integer> dim;					//stores every dim size
    	public int totalSize;					//total array size
    	public int subpart;
    	public ArrayList<Integer>  l;					//lower 
    	public int[] ixa;
    	
    	
    	public Array_info(){
    		dim= new ArrayList<Integer>();
    		typeSize=0;
    		totalSize=0;
    		subpart=0;
    		ixa=null;
    		l= new ArrayList<Integer>();
    		type="";
    	}
	}
    
    static final class Variable{
        // Think! what does a Variable contain?x
    	public String name;
    	public  int Addr;
    	public  int size;
    	public String type;
    	public String pName;
    	public Array_info a_info;
    	public int r_size;
    	
    	
    	public Variable(String name, int addr, String type, String pName) {
			this.name = name;
			Addr = addr;
			this.type = type;
			this.pName = pName;
			a_info=null;
			size=0;
			r_size=0;
			
		}
    	
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Variable other = (Variable) obj;
			if (Addr != other.Addr)
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
    	
    }
    
    static final class SymbolTable{
        // Think! what does a SymbolTable contain?
        public static int ADR;
        public static int LABEL;
        public static Stack<Integer> lStack;
//        public static int CASE_ID;
        public static ArrayList<Variable> ST;
        public SymbolTable(){
        	LABEL=0;
        	ADR=5;
//        	CASE_ID=0;
        	ST =  new ArrayList<Variable>();
        	lStack= new Stack<Integer>();
        	
        }
        public void printST(){
        	System.out.println("name:\t\tadrs\t\ttype\t\tsize\t\tptype\t\trsize");
        	for(Variable v: ST){
        		System.out.println(v.name + "\t\t" + v.Addr + "\t\t"+ v.type +"\t\t"+v.size+ "\t\t" + v.pName + "\t\t"+v.r_size);
        		if(v.a_info!=null){
        			System.out.println(v.a_info.toString());
        		}
        	}
        	
        }
    	private static void coded(AST ast) {
    		// TODO Auto-generated method stub
    		if (debug) System.out.println("coded ");
    		if(ast==null)
    			return;
            if (debug) System.out.println(ast.value);
            
            switch(ast.value){
            	case("content"):
            		coded(ast.left);
            		break;
            	case("scope"):
    				coded(ast.left);
            		break;
    			case("declarationsList"):
	    			coded(ast.left);
	    			coded(ast.right);
	    			break;
    			case("var"):
    				String pName="";
    			int curAdr=ADR;
    			int size=0;
    			Array_info inf =null;
    			int rs=0;
	    			switch(ast.right.value){
		    			case "int": 
//		    				ADR+=1;
		    				size=1;
		    			break;
		    			case "real": 
//		    				ADR+=1; 
		    				size=1;
		    			break;
		    			case "bool": 
//		    				ADR+=1;
		    				size=1;
		    			break;
		    			case "pointer": 
//		    				ADR+=1; 
		    				size=1;
		    				if(ast.right.left.value.equals("identifier"))
		    					pName=ast.right.left.left.value;
		    				else
		    					pName=ast.right.left.value;
		    			break;
		    			case "array": 
		    				inf =  new Array_info(); 
		    				if(ast.right.right.value.equals("identifier"))
		    					inf.type= ast.right.right.left.value;
		    				else
		    					inf.type=ast.right.right.value;
		    				inf.typeSize=getTypeSize(inf.type);
		    			   	genArrInfo(ast.right.left,inf);
		    			   	calcArrInfo(inf);
		    			   	size=inf.totalSize;
		    			break;
		    			case "record":
	    					coded(ast.right.left);
	    					rs = ADR-curAdr;
	    					size=rs;
		    				
	    				break;
		    			default:
		    				System.out.println("unknown coded type: " +ast.right.value);
		    				break;
	    			}
	    			ADR+=size-rs;
	    			Variable v = new Variable(ast.left.left.value,curAdr,ast.right.value,pName);
	    			v.size=size;
	    			v.r_size=rs;
	    			v.a_info=inf;
    				ST.add(v);
	    			break;
	    		default:
	    			System.out.println("unknown coded: "+ast.value);
	    			break;
            }
    	}

    	public static void calcArrInfo(Array_info inf) {
			// TODO Auto-generated method stub
    		int sub=0;
    		for(int i=0;i<inf.l.size();i++){
    			int tmp=inf.l.get(i);
    			for(int j =inf.l.size()-1;j>i;j--){
    				tmp*=inf.dim.get(j);
    			}
    			sub+=tmp;
    		}
    		inf.subpart=sub*inf.typeSize;
    		inf.ixa=new int[inf.dim.size()];
    		for(int i=0;i<inf.dim.size();i++){
    			inf.ixa[i]=inf.typeSize;
    			for(int j =inf.dim.size()-1;j>i;j--){
    				inf.ixa[i]*=inf.dim.get(j);
    			}
    		}
    		
    		inf.totalSize=inf.typeSize;
    		for(int i=0;i<inf.dim.size();i++){
    			inf.totalSize*=inf.dim.get(i);
    		}
    		
    		
    		
			
		}
		public static int getTypeSize(String type) {
			// TODO Auto-generated method stub
			for(Variable var : ST){
				if(var.name.equals(type))
					return var.size;
			}
    		return 1;
		}
		private static void genArrInfo(AST ast,Array_info inf){
			// TODO Auto-generated method stub
    		if(ast==null)
    			return;
    		genArrInfo(ast.left, inf);
    		int l = Integer.parseInt(ast.right.left.left.value);
    		int u = Integer.parseInt(ast.right.right.left.value);
    		inf.dim.add(u-l+1);
    		inf.l.add(l);
    		
    			
    			
			return ;
		}
		public static SymbolTable generateSymbolTable(AST tree){
            // TODO: create SymbolTable from AST
        	SymbolTable st=new SymbolTable();
        	coded(tree.right);
        	return st;
        }
		public static SymbolTable generateRecordTable(AST tree){
            // TODO: create SymbolTable from AST
        	SymbolTable st=new SymbolTable();
        	coded(tree.right);
        	return st;
        }
	
		public static int getAddr(String name) {
				// TODO Auto-generated method stub
				for(Variable var : ST){
					if(var.name.equals(name))
						return var.Addr;
				}
				return -1;
			} 
		public static String getPName(String name) {
			// TODO Auto-generated method stub
			for(Variable var : ST){
				if(var.name.equals(name))
					return var.pName;
			}
			return null;
		} 
		public static Variable getVar(String name) {
			// TODO Auto-generated method stub
			for(Variable var : ST){
				if(var.name.equals(name))
					return var;
			}
			return null;
		}
    }
    

    private static void generatePCode(AST ast, SymbolTable symbolTable) {
        // TODO: go over AST and print code
    	if (debug) System.out.println("generatePcode ");
    	if(ast==null)
			return;
        if (debug) System.out.println(ast.value);
        
    	code(ast,symbolTable);
    }
    private static int codec(AST ast, SymbolTable symbolTable, int la){
		if(ast==null)
			return 0;
    	int c=0;
    	switch(ast.value) {
    	case("caseList"):
    		c=codec(ast.left,symbolTable,la);
    		c++;
    		System.out.println("case_"+la+"_"+c+":");
    		code(ast.right.right,symbolTable);
    		System.out.println("ujp switch_end_"+la);
    		
//    		System.out.println("ujp case_"+la+"_"+lb);
    	break;
		default:
			System.out.println("unknown codec: "+ast.value);
		break;
    	}
    	return c;
    }
    private static void code(AST ast, SymbolTable symbolTable) {
		// TODO Auto-generated method stub
    	if (debug) System.out.println("code ");
    	if(ast==null)
			return;
        if (debug) System.out.println(ast.value);
        
        switch(ast.value){
        case("program"):
        	
     		if(ast.right!=null){
    			code(ast.right ,symbolTable);
    		}     
        	break;
        case("content"):
			code(ast.right ,symbolTable);
    		break;
        case("statementsList"):
    		code(ast.left,symbolTable);
    		code(ast.right,symbolTable);
    		break;
        case("assignment"):
    		codel(ast.left,symbolTable);
    		coder(ast.right,symbolTable);
    		System.out.println("sto");
    		break;
        case("print"):
    		coder(ast.left,symbolTable);
    		System.out.println("print");
    		break;
        case("if"):
        	if(ast.right.value.equals("else")){

        		int la=SymbolTable.LABEL++;
        		int lb=SymbolTable.LABEL++;
        		
        		coder(ast.left,symbolTable);
        		System.out.println("fjp "+"skip_if_"+la);
        		
        		code(ast.right.left,symbolTable);
        		System.out.println("ujp "+"skip_else_"+lb);
        		
        		System.out.println("skip_if_"+la+":");
        		
        		code(ast.right.right,symbolTable);
        		System.out.println("skip_else_"+lb+":");
        		
        	}
        	else{
        		int la=SymbolTable.LABEL++;
        		coder(ast.left,symbolTable);
        		System.out.println("fjp "+"skip_if_"+la);
        		
        		code(ast.right,symbolTable);
        		System.out.println("skip_if_"+la+":");
        		
        	}
    	break;
        case("while"):{
        	int la=SymbolTable.LABEL++;
        	int lb=SymbolTable.LABEL++;
        	SymbolTable.lStack.push(lb);
        	System.out.println("while_"+la+":");
        	coder(ast.left,symbolTable);
        	System.out.println("fjp "+"while_out_"+lb);
        	code(ast.right,symbolTable);
        	System.out.println("ujp "+"while_"+la);
        	System.out.println("while_out_"+lb+":");
        break;}
        case("switch"):{
        	int la=SymbolTable.LABEL++;
        	coder(ast.left,symbolTable);
	        System.out.println("neg");
	        System.out.println("ixj switch_end_"+la);
        	int c =codec(ast.right,symbolTable,la);
        	for(int i=c ;i>0;i--){
        		System.out.println("ujp case_"+la+"_"+i);
        	}
        	System.out.println("switch_end_"+la+":");
//        	System.out.println("L"+la+":");
        break;}
        case("break"):{
        		int lb =SymbolTable.lStack.pop();
        		System.out.println("ujp while_out_"+lb);
        		
        break;
        }
        
    	default:
    		System.out.println("unknown code: "+ast.value);
    	break;
        }
	}

	private static String codel(AST ast, SymbolTable symbolTable) {
		// TODO Auto-generated method stub
		if (debug) System.out.println("codel ");
		if(ast==null)
			return null;
        if (debug) System.out.println(ast.value);
        switch(ast.value){
	        case("identifier"):
	        	System.out.println("ldc " + SymbolTable.getAddr(ast.left.value));
	        	return ast.left.value;
	        	
	        case("pointer"):
	        	String name = codel(ast.left,symbolTable);
	        	String pname= SymbolTable.getPName(name);
	        	System.out.println("ind");
	        	return pname;
	        
	        case ("array"):
	        		String n =codel(ast.left,symbolTable);
	        		Variable v=SymbolTable.getVar(n);
	        		printArrInfo(v.a_info,ast.right,symbolTable);
					System.out.println("dec "+v.a_info.subpart);
					return v.a_info.type;
//		    	for(Variable var :  SymbolTable.ST){
//		    		String s1=var.name;
//		    		String s2=ast.left.left.value;
////					if(var.name==ast.left.left.value){
//		    		if(s1.equals(s2)){
//		    			System.out.println("ldc " + var.Addr);
//						printArrInfo(var.a_info,ast.right,symbolTable);
//						System.out.println("dec "+var.a_info.subpart);
//						break;
//					}
//				}
	        case ("record"):
	        		int adr1 = SymbolTable.getAddr(codel(ast.left,symbolTable));
	        		int adr2 = SymbolTable.getAddr(ast.right.left.value);
	        		adr2-=adr1;
	        		System.out.println("inc "+ adr2);
	        		return ast.right.left.value;
	        default:
	        	System.out.println("unknown codel: "+ast.value);
			break;
        }
        return null;
	}

	public static int printArrInfo(Array_info info, AST ast ,SymbolTable symbolTable) {
		// TODO Auto-generated method stub
		if(ast==null)
			return 0;
		int i = printArrInfo(info, ast.left,symbolTable);
		coder(ast.right,symbolTable);
		System.out.println("ixa "+info.ixa[i]);
		return i+1;
	}
	private static void coder(AST ast, SymbolTable symbolTable) {
		if (debug) System.out.println("coder ");
		if(ast==null)
			return;
        if (debug) System.out.println(ast.value);
        
        switch(ast.value){
        case("false"):
        	System.out.println("ldc 0");
        break;
        case("true"):
        	System.out.println("ldc 1");
        break;
        case("constInt"):
			System.out.println("ldc " + ast.left.value);
        break;
        case("constReal"):
			System.out.println("ldc " + ast.left.value);
        break;
        case("not"):
        	coder(ast.left,symbolTable);
			System.out.println("not");
        break;
        case("negative"):
        	coder(ast.left,symbolTable);
			System.out.println("neg");
        break;
        case("identifier"):
        	codel(ast,symbolTable);
        	System.out.println("ind");
        break;
        case("plus"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("add");
        break;
        case("minus"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("sub");
        break;
        case("or"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("or");
        break;
        case("and"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("and");
        break;
        case("multiply"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("mul");
        break; 
        case("divide"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("div");
        break;        
        case("lessThan"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("les");
        break;      
        case("greaterThan"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("grt");
        break;        
        case("lessOrEquals"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("leq");
        break;        
        case("greaterOrEquals"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("geq");
        break;        
        case("notEquals"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("neq");
        break;      
        case("equals"):
        	coder(ast.left,symbolTable);
        	coder(ast.right,symbolTable);
        	System.out.println("equ");
        break;
        case ("array"):
        	codel(ast,symbolTable);
        	System.out.println("ind");
    	break;
        case ("record"):
        	codel(ast,symbolTable);
    		System.out.println("ind");
    		break;
        default:
        	System.out.println("unknown coder: "+ast.value);
    	break;
		}
	}

//	public static void main(String[] args) {
	public static void main(String[] args) throws FileNotFoundException {

//    	Scanner scanner = new Scanner(new File("input\\tree8.txt"));
    	Scanner scanner = new Scanner(System.in);
        AST ast = AST.createAST(scanner);
        SymbolTable symbolTable = SymbolTable.generateSymbolTable(ast);
//        symbolTable.printST();
        generatePCode(ast, symbolTable);
    

    }

}