package av_heap;

import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;

public class K_ClosestPointsToOrigin {
	
	static class DistanceWithPoint implements Comparable<DistanceWithPoint>{
		
		public double dist;
		public Pair point;
		
		DistanceWithPoint(double dist, Pair point) {
			super();
			this.dist = dist;
			this.point = point;
		}

		@Override
		public int compareTo(DistanceWithPoint anotherDistanceWithPoint) {
			double diff = this.dist - anotherDistanceWithPoint.dist;
			if (diff > 0) {
				return 1;
			} else if (diff < 0) {
				return -1;
			} else {
				return 0;
			}
		}

	}
	
	public static int[][] k_ClosestPointsToOrigin(int[][] arrOfPoints, int k) {
		int[][] cloestPoints = new int[k][2];
		PriorityQueue<DistanceWithPoint> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
		for (int i = 0; i < arrOfPoints.length; i++) {
			double dist = Math.pow(arrOfPoints[i][0], 2) + Math.pow(arrOfPoints[i][1], 2);
			DistanceWithPoint distanceWithPoint = new DistanceWithPoint(dist, new Pair(arrOfPoints[i][0], arrOfPoints[i][1]));
			maxHeap.add(distanceWithPoint);
			if (maxHeap.size() > k) {
				maxHeap.poll();
			}
		}
		for (int i = 0; i < k; i++) {
			DistanceWithPoint distanceWithPoint = maxHeap.poll();
			cloestPoints[i][0] = distanceWithPoint.point.a;
			cloestPoints[i][1] = distanceWithPoint.point.b;
		}
		return cloestPoints;
	}

}
