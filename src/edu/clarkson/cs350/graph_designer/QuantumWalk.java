package edu.clarkson.cs350.graph_designer;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class QuantumWalk {
	private static int n;
	
	public static Array2DRowFieldMatrix<Complex> qwalk(RealMatrix A, double t){
    	Object[] specDecompResult = specdecomp(A);
    	double[] eigenvalues = arrayListToDoubleArray((ArrayList)specDecompResult[0]);
    	ArrayList<RealMatrix> eigenprojectors = (ArrayList) specDecompResult[1];
    	
    	Array2DRowFieldMatrix<Complex> U = new Array2DRowFieldMatrix<Complex>(zeroGen(n, n));

    	Complex neg1j = new Complex(0.0, -1.0);
    	Complex[][][] eigVmult = toComplex(eigenprojectors);
    	
    	for (int j=0; j<eigenvalues.length; j++){
    		Complex exp = (neg1j.multiply(new Complex(t * eigenvalues[j]))).exp();
    		Array2DRowFieldMatrix<Complex> complexEigJ =
    				new Array2DRowFieldMatrix<Complex>(eigVmult[j]);
    		
    		Array2DRowFieldMatrix<Complex> toAdd = multiplyConst(exp, complexEigJ);
    		
    		U = addMatrices(U,toAdd);
    	}
    	
    	return U;
    }
	
	public static void setN(int n){
		QuantumWalk.n = n;
	}
	public static int getN(){
		return n;
	}
    
    private static double[] arrayListToDoubleArray(ArrayList<Double> al){
    	double[] ret = new double[al.size()];
    	for (int i=0; i<al.size(); i++){
    		ret[i] = al.get(i);
    	}
    	return ret;
    }
    
    private static ArrayList[] specdecomp(RealMatrix A){
    	EigenDecomposition eig = new EigenDecomposition(A);
    	double[] eigenvalue_list = eig.getRealEigenvalues();
    	RealMatrix row_eigenmatrix = eig.getV();
    	int num_rows = n;
    	int num_cols = n;
    	
    	RealMatrix eigenmatrix = row_eigenmatrix.transpose();
    	
    	ArrayList<Double> eigenvalues = new ArrayList<Double>();
    	ArrayList<RealMatrix> eigenprojectors = new ArrayList<RealMatrix>();
    	
    	RealMatrix v = MatrixUtils.createRealMatrix(n, 1);
    	
    	for (int i=0; i<num_rows; i++){
    		boolean found = false;
    		for (int j=0; j<eigenvalues.size(); j++){
    			if (Math.abs(eigenvalue_list[i] - eigenvalues.get(j)) < 0.0001){
    				v.setRow(0, eigenmatrix.getRow(i));
    				eigenprojectors.set(j,(eigenprojectors.get(j)).add(v.multiply(v.transpose())));
    				found = true;
    			}
    		}
    		if (!found){
    			eigenvalues.add(eigenvalue_list[i]);
    			v.setColumn(0, eigenmatrix.getRow(i));
    			eigenprojectors.add(v.multiply(v.transpose()));
    		}
    	}
    	
    	return new ArrayList[]{eigenvalues,eigenprojectors};
    }
    
    private static Complex[][][] toComplex(ArrayList<RealMatrix> d){
    	int size1 = d.size();
    	int size2 = d.get(0).getColumn(0).length;
    	int size3 = d.get(0).getRow(0).length;
    	
    	Complex[][][] ret = new Complex[size1][size2][size3];
    	
    	for (int i=0; i<size1; i++){
    		for (int j=0; j<size2; j++){
    			for (int k=0; k<size3; k++){
    				ret[i][j][k] = new Complex(d.get(i).getEntry(j, k));
    			}
    		}
    	}
    	return ret;
    }
    
    private static Complex[][] zeroGen(int x, int y){
    	Complex[][] ret = new Complex[x][y];
    	for (int i=0; i<x; i++){
    		for (int j=0; j<y; j++){
        		ret[i][j] = new Complex(0.0);
        	}
    	}
    	return ret;
    }
    
    private static Array2DRowFieldMatrix<Complex> multiplyConst(Complex c, Array2DRowFieldMatrix<Complex> matrix){
    	int rowSize = matrix.getRowDimension();
    	int colSize = matrix.getColumnDimension();
    	
    	Complex[][] newMatrix = new Complex[rowSize][colSize];
    	
    	for (int i=0; i<rowSize; i++){
    		for (int j=0; j<colSize; j++){
    			newMatrix[i][j] = matrix.getEntry(i, j).multiply(c);
    		}
    	}
    	
    	return new Array2DRowFieldMatrix<Complex>(newMatrix);
    }
    
    private static Array2DRowFieldMatrix<Complex> addMatrices(Array2DRowFieldMatrix<Complex> m1, Array2DRowFieldMatrix<Complex> m2){
    	int rowSize = m1.getRowDimension();
    	int colSize = m2.getColumnDimension();
    	
    	Complex[][] ret = zeroGen(rowSize, colSize);
    	
    	for (int i=0; i<rowSize; i++){
    		for (int j=0; j<colSize; j++){
    			ret[i][j] = m1.getEntry(i, j).add(m2.getEntry(i, j));
    		}
    	}
    	return new Array2DRowFieldMatrix<Complex>(ret);
    }
}
