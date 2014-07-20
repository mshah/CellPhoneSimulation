final public class Matrix{
    public final int M;        // number of rows
    public final int N;        // number of columns
    private final int[][] data;  // M-by-N array

    // create M-by-N matrix of 0's
    public Matrix(int M, int N){
        this.M = M;
        this.N = N;
        data = new int[M][N];
    }

    // set elements
    public void SetValue(int Mcoord, int Ncoord, int value){
        data[Mcoord][Ncoord] = value;
    }

    // get elements
    public int GetValue(int Mcoord, int Ncoord){
        if (Mcoord < 0 || Mcoord > M)
            return 0;
        if (Ncoord < 0 || Ncoord > N)
            return 0;
        int outValue = data[Mcoord][Ncoord];
        return outValue;
    }

    // print matrix to standard output
    public void show(){
        for (int i = 0; i < M; i++){
            for (int j = 0; j < N; j++){
                System.out.printf("%9.4f ", data[i][j]);
                System.out.println();
            }
        }
    }
}