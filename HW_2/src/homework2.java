
//terkel and jos hw1

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
		
		public int dim ;					// dim
    	public int g;                      // typeSize
    	public ArrayList<Integer> di;					//stores every dim size
    	public int size;					//total array size
    	public int subpart;
    	public int[] ixa;
    	
    	
    	public Array_info(){
    		di= new ArrayList<Integer>();
    		dim=0;
    		g=0;
    		size=0;
    		subpart=0;
    		ixa=null;
    		
    	}
	}
    
    static final class Variable{
        // Think! what does a Variable contain?x
    	public String name;
    	public  int Addr;
    	public String type;
    	public String pName;
    	public Array_info a_info;
    	
    	public Variable(String name, int addr, String type, String pName) {
			this.name = name;
			Addr = addr;
			this.type = type;
			this.pName = pName;
			a_info=null;
		}
    	
    	/**
		 * @param a_info the a_info to set
		 */
		public void setA_info(Array_info a_info) {
			this.a_info = a_info;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		
	
		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}
		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}
		/**
		 * @return the addr
		 */
		public int getAddr() {
			return Addr;
		}
		/**
		 * @param addr the addr to set
		 */
		public void setAddr(int addr) {
			Addr = addr;
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
//        public static int CASE_ID;
        public static ArrayList<Variable> ST;
        public SymbolTable(){
        	LABEL=0;
        	ADR=5;
//        	CASE_ID=0;
        	ST =  new ArrayList<Variable>();
        	
        }
        public void printST(){
        	System.out.println("name:\t\tadrs\t\ttype\t\tptype");
        	for(Variable v: ST){
        		System.out.println(v.name + "\t\t" + v.Addr + "\t\t"+ v.type + "\t\t" + v.pName);
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
    			Array_info inf =null;
	    			switch(ast.right.value){
		    			case "int": 
		    				ADR+=1; 
		    			break;
		    			case "real": 
		    				ADR+=1; 
		    			break;
		    			case "bool": 
		    				ADR+=1; 
		    			break;
		    			case "pointer": 
		    				ADR+=1; 
		    				pName=ast.right.left.value;
		    			break;
		    			case "array": inf =  new Array_info(); 
		    			   		inf= codec_a(ast.right.left,inf);
		    			break;
		    			default:
		    				System.out.println("unknown coded type: " +ast.right.value);
		    				break;
	    			}
	    			Variable v = new Variable(ast.left.left.value,curAdr,ast.right.value,pName);
	    			v.a_info=inf;
    				ST.add(v);
	    			break;
	    		default:
	    			System.out.println("unknown coded: "+ast.value);
	    			break;
            }
    	}

    	private static Array_info codec_a(AST ast,Array_info inf){
			// TODO Auto-generated method stub
    		switch(ast.left.value){
    		case "rangeList":	; break;
    		case "range": 		 break;
    		case "constInt":     break;
    		default:
    			System.out.println("unknown coded_a: "+ast.value);
    			break;
    		}	
			return inf;
		}
		public static SymbolTable generateSymbolTable(AST tree){
            // TODO: create SymbolTable from AST
        	SymbolTable st=new SymbolTable();
        	coded(tree.right);
        	return st;
        }
	
	public static int getAddr(String name) {
			// TODO Auto-generated method stub
			for(Variable var : ST){
				if(var.getName().equals(name))
					return var.getAddr();
			}
			return -1;
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
    private static void codec(AST ast, SymbolTable symbolTable, int la, int lb){
		if(ast==null)
			return;
    	
    	switch(ast.value) {
    	case("caseList"):
    		System.out.println("case_"+la+"_"+lb+":");
    		code(ast.right.right,symbolTable);
    		System.out.println("ujp switch_end_"+la);
    		codec(ast.left,symbolTable,la,lb+1);
    		System.out.println("ujp case_"+la+"_"+lb);
    	break;
		default:
			System.out.println("unknown codec: "+ast.value);
		break;
    	}
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
        	System.out.println("L"+la+":");
        	coder(ast.left,symbolTable);
        	System.out.println("fjp "+"L"+lb);
        	code(ast.right,symbolTable);
        	System.out.println("ujp "+"L"+la);
        	System.out.println("LW"+lb+":");
        break;}
        case("switch"):{
        	int la=SymbolTable.LABEL++;
        	coder(ast.left,symbolTable);
	        System.out.println("neg");
	        System.out.println("ixj switch_end_"+la);
        	codec(ast.right,symbolTable,la,1);
        	System.out.println("switch_end_"+la+":");
//        	System.out.println("L"+la+":");
        break;}
    	default:
    		System.out.println("unknown code: "+ast.value);
    	break;
        }
	}

	private static void codel(AST ast, SymbolTable symbolTable) {
		// TODO Auto-generated method stub
		if (debug) System.out.println("codel ");
		if(ast==null)
			return;
        if (debug) System.out.println(ast.value);
        switch(ast.value){
        case("identifier"):
        	System.out.println("ldc " + SymbolTable.getAddr(ast.left.value));
        break;
        case("pointer"):
        	codel(ast.left,symbolTable);
        	System.out.println("ind");
        break;
        
        default:
        	System.out.println("unknown codel: "+ast.value);
		break;
        }
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
        default:
        	System.out.println("unknown coder: "+ast.value);
    	break;
		}
	}

//	public static void main(String[] args) {
	public static void main(String[] args) throws FileNotFoundException {

    	Scanner scanner = new Scanner(new File("input\\tree3.txt"));
//    	Scanner scanner = new Scanner(System.in);
        AST ast = AST.createAST(scanner);
        SymbolTable symbolTable = SymbolTable.generateSymbolTable(ast);
//        symbolTable.printST();
        generatePCode(ast, symbolTable);
    

    }

}