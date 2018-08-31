// Group 3
// Tariq Almazyad
// Jon Mierzwa
// Nick Esposito
// Tyler Debrino

public class point3f
{
    public float x;
    public float y;
    public float z;

    public point3f()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public void setPolar(float theta, float phi, float r)
    {


        x = (float) (r*Math.sin(theta)* Math.cos(phi));
        y = (float) (r*Math.sin(theta)* Math.sin(phi));
        z = (float) (r*Math.cos(theta));

    }
    public point3f[] pointCloudSphere(int n, int m)
    {
        point3f[] spherePoints = new point3f[n*m];
        for(int i = 0;i < spherePoints.length;i++)
        {
            spherePoints[i] = new point3f();
        }
        float thetaPiece = 360/n;
        float phiPiece = 360/m;
        int arrayCount = 0;
        for(int i=0; i<n; i++)
        {
            float theta = (float) i * thetaPiece;
            for(int j=0; j<m; j++)
            {
                float phi = (float) j * phiPiece;
                spherePoints[arrayCount].setPolar(i*theta, i* phi, 1);
                arrayCount++;


            }
        }
        return spherePoints;
    }
    public point3f[][] generatorPointGridXZ(float Xmin, float Xmax, int nx, float Zmin, float Zmax, int nz )
    {
        point3f[][] grid = new point3f[nx][nz];
        for (int xCount = 0; xCount < nx; xCount++)
        {
            for(int zCount = 0; zCount < nz; zCount++)
            {
                float YCurr = (float) Math.sin(10 * ((Xmin*Xmin) + (Zmin * Zmin))/10);
                System.out.println(YCurr);
                System.out.println(Zmin);
                Xmin += (Xmax/nx);
                Zmin +=(Zmax/nz);
            }
        }
        return grid;
    }

    public float getX(){

        return x;
    }
    public float gety(){

        return y;
    }
    public float getz(){

        return z;
    }

}

