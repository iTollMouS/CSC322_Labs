// Group 3
// Tariq Almazyad
// Jon Mierzwa
// Nick Esposito
// Tyler Debrino

public class Point3f
{
    public float a,b,c;
    public Point3f(float val1, float val2, float val3)
    {
        a = val1;
        b = val2;
        c = val3;
    }

    // set cartesian coordinates
    public float Theta, Phi,R;
    public void setPolar(float theta, float phi, float r)
    {
        Theta = theta;
        Phi   = phi;
        R     = r;
    }

    public float[][][] pointCloudPolarSphere(int m, int n)
    {
        float[][][] result = new float[m][n][3];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                // Using intervals from setPolar method
                result[i][j][0]= R * (float)Math.cos(Theta * i) * (float)Math.sin(Phi * j);
                result[i][j][1]= R * (float)Math.sin(Theta * i) * (float)Math.sin(Phi * j);
                result[i][j][2]= R * (float)Math.cos(Phi * j);
            }
        }
        return result;
    }
}
