import java.util.ArrayList;
import java.util.HashMap;



public class KMeans
{
    private static final String FORMAT_OF_OUTPUT = "%s,%d";
    private static final String HEADER_OF_OUTPUT = "Country,Group";

    private static final double     MIN_THRESHOLD = 0.001;
    private final static double[][] table         = {{10326.00, 90.00, 23.60, 75.40}, {39650.00, 99.00, 4.08, 79.40}, {830.00, 38.70, 95.90, 42.10}, {43163.00, 99.00, 4.57, 81.20}, {5300.00, 90.90, 23.00, 73.00}, {13308.00, 97.20, 13.40, 75.30}, {34105.00, 99.00, 5.01, 79.40}, {10600.00, 82.40, 44.80, 49.30}, {1000.00, 68.00, 92.70, 42.40}, {5249.00, 85.00, 42.30, 52.90}, {4200.00, 100.00, 17.36, 71.00}, {3320.00, 49.90, 67.50, 65.50}, {2972.00, 61.00, 55.00, 64.70}, {12888.00, 88.70, 27.50, 71.80}, {34735.00, 99.00, 3.20, 80.90}, {19730.00, 99.60, 8.50, 73.00}, {36983.00, 96.00, 5.34, 79.50}, {26760.00, 98.50, 5.94, 80.00}, {34099.00, 99.00, 3.20, 82.60}};
    private final static String[] countries = {"Brazil", "Germany", "Mozambique", "Australia", "China", "Argentina", "United Kingdom", "South Africa", "Zambia", "Namibia", "Georgia", "Pakistan", "India", "Turkey", "Sweden", "Lithuania", "Greece", "Italy", "Japan"};
    private volatile static double[][] centroids = {{10326.00, 90.00, 23.60, 75.40}, {39650.00, 99.00, 4.08, 79.40}, {830.00, 38.70, 95.90, 42.10}};

    public static void main(String[] args)
    {
        System.out.println("==========Before Normalization==========");
        run();

        System.out.println("==========After Normalization==========");
        normalize(table);
        run();

    }

    public static void run()
    {
        final int K = centroids.length;

        double[][]                           initialCentroids;
        HashMap<Integer, ArrayList<Integer>> map;
        do
        {
            initialCentroids = centroids.clone();
            map = kmeans(centroids, table);
        } while (!isRepeat(K, initialCentroids, centroids, MIN_THRESHOLD));

        StringBuilder sb = new StringBuilder(HEADER_OF_OUTPUT);
        sb.append("\n");
        sb.append("==================\n");
        for (int k = 0; k < K; k++)
        {
            ArrayList<Integer> indexes = map.get(k);
            for (int i : indexes)
            {
                String country = countries[i];
                sb.append(String.format(FORMAT_OF_OUTPUT, country, k));
                sb.append("\n");
            }
        }

        System.out.println(map.toString());
        System.out.println(sb.toString());


    }


    public static boolean isRepeat(int K, double[][] pre, double[][] last, double minThreshold)
    {
        for (int k = 0; k < K; k++)
        {
            if (diff(pre[k], last[k]) < minThreshold)
            {
                return true;
            }
        }
        return false;
    }

    public static double diff(double[] lhs, double[] rhs)
    {
        return distance(lhs, rhs);
    }

    public static HashMap<Integer, ArrayList<Integer>> kmeans(double[][] centroids, double[][] table)
    {
        final int K = centroids.length;
        final int N = table.length;
        final int C = table[0].length;

        final HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
        for (int k = 0; k < K; k++)
        {
            map.put(k, new ArrayList<Integer>());
        }

        final double[][] distance = new double[N][K];
        for (int k = 0; k < K; k++)
        {
            double[] centroid = centroids[k];
            for (int i = 0; i < N; i++)
            {
                double[] row = table[i];
                distance[i][k] = distance(centroid, row);
            }
        }

        for (int i = 0; i < N; i++)
        {
            int w = argMin(distance[i]);
            map.get(w).add(i);
        }

        final double[][] newCentroids = new double[K][C];
        for (int k = 0; k < K; k++)
        {
            ArrayList<Integer> indexes = map.get(k);
            double[] newCentroid = findNewCentroid(indexes, table);
            newCentroids[k] = newCentroid;
        }
        centroids = newCentroids;

        return map;
    }

    public static double[] findNewCentroid(ArrayList<Integer> indexes, double[][] table)
    {
        int      C           = table[0].length;
        double[] newCentroid = new double[C];
        for (int c = 0; c < C; c++)
        {
            int size = indexes.size();
            if (size == 0)
            {
                newCentroid[c] = 0.0;
            } else
            {
                double tmp = 0.0;
                for (int i : indexes)
                {
                    double[] row = table[i];
                    tmp += row[c];
                }
                newCentroid[c] = tmp / (double) indexes.size();
            }
        }
        return newCentroid;
    }

    public static double sum(double[] data)
    {
        double tmp = 0.0;
        for (int i = 0; i < data.length; i++)
        {
            tmp += data[i];
        }

        return tmp;
    }

    public static double average(double[] data)
    {
        if (data.length == 0)
            return 0;

        double s = sum(data);
        return s / data.length;
    }

    public static double distance(double[] lhs, double[] rhs)
    {
        double tmp = 0.0;
        for (int i = 0; i < lhs.length; i++)
        {
            tmp += Math.pow(lhs[i] - rhs[i], 2);
        }
        return Math.sqrt(tmp);
    }

    public static double min(double[] data)
    {
        double m = Double.MAX_VALUE;

        for (int i = 0; i < data.length; i++)
        {
            if (data[i] < m)
                m = data[i];
        }
        return m;
    }

    public static int argMin(double[] data)
    {
        double m     = Double.MAX_VALUE;
        int    index = 0;
        for (int i = 0; i < data.length; i++)
        {
            if (data[i] < m)
            {
                m = data[i];
                index = i;
            }
        }
        return index;
    }


    public static double findMin(int c, double[][] table)
    {
        int    N   = table.length;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < N; i++)
        {
            if (table[i][c] < min)
            {
                min = table[i][c];
            }
        }
        return min;
    }


    public static double findMax(int c, double[][] table)
    {
        int    N   = table.length;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < N; i++)
        {
            if (table[i][c] > max)
            {
                max = table[i][c];
            }
        }
        return max;
    }


    public static void normalize(double[][] table)
    {
        int C = table[0].length;
        for (int c = 0; c < C; c++)
        {
            normalize(c, table);
        }
    }

    public static void normalize(int c, double[][] table)
    {
        int    N   = table.length;
        double max = findMax(c, table);
        double min = findMin(c, table);

        for (int i = 0; i < N; i++)
        {
            double newValue = (table[i][c] - min) / (max - min);
            table[i][c] = newValue;
        }
    }
}
