//import statements
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

public class MarvelGraph{
	public static void main(String[] args) throws Exception{
		Graph Marvel = new Graph();
		CSVReader.nodesRead(args[0], Marvel);    //read the file containing vertices and store them
		CSVReader.edgesRead(args[1], Marvel);    //read the file containing edges and create an adjacency list
		switch(args[2]){
			case "average":
				System.out.println(String.format("%.2f", Marvel.average()));
				//print the average no of characters associated with a character
				break;
			case "rank":
				Marvel.gen_co();
				int[] ranks = Marvel.rank();
				if(ranks.length == 0){break;}
				System.out.print(Marvel.vertices.get(ranks[0]).label);
				for(int i = 1; i < Marvel.size; i++){System.out.print("," + Marvel.vertices.get(ranks[i]).label);}
				break;
			case "independent_storylines_dfs":
				Marvel.independent_storylines_dfs();
				for(int i = 0; i < Marvel.connected_components.size(); i++){
					int[] comp = Marvel.connected_components.get(Marvel.sort_comp.get(Marvel.connected[i]));
					for(int j = 0; j < comp.length - 1; j++){
						System.out.print(Marvel.vertices.get(comp[j]).label + ",");
					}
					System.out.println(Marvel.vertices.get(comp[comp.length - 1]).label);
				}
				break;
		}
	}
}

class CSVReader{
	static void nodesRead(String name, Graph G) throws Exception{
		//This function reads the file containing vertices and numbers them while updating the list of vertices
		//Time Complexity:: O(n); n = number of vertices
		//Space Complexity:: O(n); n = number of vertices
		
		String id, label, temp;    //temporary dynamic variables
		Vertex v;    //pointer for vertex
		G.indices = new HashMap<String, Integer>();
		G.vertices = new ArrayList<Vertex>();    //initiate the Hashmaps and Arraylists
		int count = 0;    //counter for number of vertices
		Scanner read = new Scanner(new File(name));    //define a new scanner object to read nodes' file
		read.useDelimiter(",|\\n");    //Separate the file by using , or \n as separators
		read.next();
		read.next();    //ignore the headers, i.e. id and label
		while(read.hasNext()){
			//reading the id
			id = read.next();
			if(id.charAt(0) == '"'){
				temp = read.next();
				id = id.substring(1) + "," + temp.substring(0, temp.length() - 1);
			}
			//reading the label
			label = read.next();
			if(label.charAt(0) == '"'){
				temp = read.next();
				label = label.substring(1) + "," + temp.substring(0, temp.length() - 1);
			}
			G.indices.put(label, count);
			G.vertices.add(new Vertex(id, label, count));    //add the vertex in the hashMap and list
			count ++;    //increment the counter
		}
		read.close();
		G.size = count;    //modify the total number of nodes in the graph
	}
	
	static void edgesRead(String name, Graph G) throws Exception{
		//This function reads the file containing edges (co-occurences)
		//and updates the adjacency list
		//Time Complexity:: O(m); m = number of edges
		//Space Complexity:: O(m); m = number of edges
		
		String source, target, temp;
		Vertex v1, v2;
		int weight, count;
		G.adjList = new ArrayList<LinkedList<Edge>>();
		for(int i = 0; i < G.size; i++){G.adjList.add(new LinkedList<Edge>());}    //initialise the adjacency list
		Scanner read = new Scanner(new File(name));    //define a new scanner object to read edges' file
		read.useDelimiter(",|\\n");    //Separate the file by using , or \n as separators
		read.next();
		read.next();
		read.next();    //ignore the headers, i.e. source, target and weight
		count = 0;
		while(read.hasNext()){
			//reading the source label
			source = read.next();
			if(source.charAt(0) == '"'){
				temp = read.next();
				source = source.substring(1) + "," + temp.substring(0, temp.length() - 1);
			}
			//reading the target label
			target = read.next();
			if(target.charAt(0) == '"'){
				temp = read.next();
				target = target.substring(1) + "," + temp.substring(0, temp.length() - 1);
			}
			//reading the weight
			weight = Integer.parseInt(read.next());
			v1 = G.vertices.get(G.indices.get(source));    //store the source vertex
			v2 = G.vertices.get(G.indices.get(target));    //store the target vertex
			for(int k = 0; k < G.adjList.get(v1.index).size(); k++){
				if(G.adjList.get(v1.index).get(k).end[1] == v2){
					continue;    //ignore the edge if it has already been added
				}
			}
			//create two edges and parse these in the adjacency list of v1 and v2
			G.adjList.get(G.indices.get(source)).add(new Edge(v1, v2, weight));
			G.adjList.get(G.indices.get(target)).add(new Edge(v2, v1, weight));
			count ++;
		}
		read.close();
		G.edgesize = count;
	}
}

class Graph{
	//this class stores the main graph
	int size, edgesize;    //integer to store number of vertices and edges in the graph
	HashMap<String, Integer> indices;    //this hashmap stores the index for each label of a vertex
	ArrayList<Vertex> vertices;    //this list stores the vertices at correct index
	ArrayList<LinkedList<Edge>> adjList;    //the list shall store the linked lists containing the adjacenct list of each vertex
	int[] co_occ;    //this list shall contain total no of coocc of each character stored by order
	ArrayList<int[]> connected_components;    //ArrayList containing connected components of the graph
	int[] connected;    //array containing first elements of connected components in reverse sorted order
	HashMap<Integer, Integer> sort_comp;    //hashmap to help in sorting components
	void Graph(){}    //constructor for Graph
	
	public float average(){
		//returns the average number of characters associated
		//Time Complexity:: O(1), constant number of operations
		//Space Complexity:: O(1)
		
		return (size == 0) ? -1.0f : (float) Math.round((float)(2 * edgesize)/size * 100)/100;
		//return null for 0 size of the list
		//otherwise return average no of characters associated with each
		//where average no of characters = 2* total no of edges / no of vertices
		//since 2*total no of edges = sum of degrees of all vertices
		//which is in this case the average no of characters associated
	}
	
	public int[] rank(){
		//returns a list of vertices ordered by the decreasing no of co-occurences
		//in case of same no of co-occurences the ordering is lexicographical
		//Time Complexity:: O(n log n)    where n = number of vertices in Graph
		//Space Complexity:: O(n)
		
		int[] arr = new int[this.size];
		for(int i = 0; i < size; i++){arr[i] = i;}    //create an array of the indices from 0 to size - 1
		Heap.sort(arr, this, this.size, true, co_occ);    //Heap sort the array by the required ordering
		return arr;    //return the array with reverse sorted indices
	}
	
	public void dfs(int v, ArrayList<Integer> component, boolean[] visited){
		//performs a depth-first search on the node v of graph
		//adds in the components arraylist
		
		component.add(v);    //add the vertex in the component list
		visited[v] = true;
		for(int i = 0; i < adjList.get(v).size(); i++){
			//loop over the adjacent vertices of v1
			if(visited[adjList.get(v).get(i).end[1].index] != true){
				dfs(adjList.get(v).get(i).end[1].index, component, visited);    //recursively call dfs over all unvisited neighbours of v
			}
		}
	}
	
	public void independent_storylines_dfs(){
		//performs dfs on the graph starting from 1st vertex
		//separates the connected components of the graph representing independent storylines
		//Time Complexity:: O(n + m)    where n and m are no of vertices and edges
		//Space Complexity:: O(n)    where n is the number of vertices
		
		boolean[] visited = new boolean[size];    //array containing the information whether a vertex has been visited or not
		connected_components = new ArrayList<int[]>();
		sort_comp = new HashMap<Integer, Integer>();
		connected = new int[size];
		for(int i = 0; i < size; i++){
			//iterate over all vertices of the graph
			if(visited[i] != true){
				//if the vertex is not visited, start dfs
				ArrayList<Integer> comp = new ArrayList<Integer>();
				dfs(i, comp, visited);    //perform dfs on i and add the connected vertices to comp
				int[] comp_arr = new int[comp.size()];
				for(int k = 0; k < comp.size(); k++){comp_arr[k] = comp.get(k);}
				Heap.sort(comp_arr, this, comp.size(), false, co_occ);
				connected_components.add(comp_arr);
				//once dfs is over, add the array to the list of components sorted lexicographically (hence occurrence = false)
			}
		}
		int[] size_comp = new int[size];    //array containing sizes of connected components
		for(int i = 0; i < connected_components.size(); i++){
			//add index-firstelement pair in Hashmap sort_comp
			sort_comp.put(connected_components.get(i)[0], i);
			connected[i] = connected_components.get(i)[0];    //create an array to store the indices of the components
			size_comp[connected_components.get(i)[0]] = connected_components.get(i).length;    //update the size array
		}
		Heap.sort(connected, this, connected_components.size(), true, size_comp);
	}
	
	public void gen_co(){
		//This function generates the list co_occ
		//Time Complexity:: O(m + n) where m = no of edges and m = no of vertices
		//Space Complexity:: O(1)
		
		co_occ = new int[size];
		for(int i = 0; i < size; i++){
			//loop through all vertices
			int sum = 0;
			for(int j = 0; j < adjList.get(i).size(); j++){
				//loop through all neighbours of vertex with index i
				sum = sum + adjList.get(i).get(j).weight;    //add the weight of each edge adjacent to the vertex i
			}
			co_occ[i] = sum;    //set the value of co_occ to sum
		}
	}
	
	boolean isLess(Vertex v1, Vertex v2, boolean occurrence, int[] size){
		//returns true/false whether a vertex v1 in given graph is lower or higher ranked
		//Time Complexity:: O(1)
		//Space Complexity:: O(1)
		
		if(occurrence && (size[v1.index] != size[v2.index])){
			//if characters have different sizes, rank by size
			return size[v1.index] < size[v2.index];
		}
		else{
			//if they have same size, rank lexicographically
			return v1.isLess(v2);
		}
	}
}

class Vertex{
	//objects of this class are vertices of the main Graph
	String id;    //stores the id of the character
	String label;    //stores the label of the character
	int index;    //stores a unique indext for that vertex
	Vertex(String i, String l, int in){
		id = i;
		label = l;
		index = in;    //constructor for vertex
	}
	
	boolean isLess(Vertex v){
		//this function returns true/false depending whether this has lexicographically lower/higher label than v
		//Time Compplexity:: O(1)
		//Space Complexity:: O(1)
		
		return this.label.compareTo(v.label) < 0;
	}
}

class Edge{
	//objects of this class are edges of the main Graph
	//these shall be stored in adjacency list
	Vertex[] end;    //stores the vertices representing end points of the edge
	//Since the graph is undirected, the ordering shall not matter
	//nonetheless the source shall be the current vertex in whose adjacency list the edge is added
	int weight;    //stores the weight of each edge
	Edge(Vertex source, Vertex target, int w){
		end = new Vertex[2];
		end[0] = source;
		end[1] = target;
		weight = w;    //constructor for edge
	}
}

class Heap{
	//Heapsort shall be implemented by means of an array
	static void swap(int[] array, int i, int j){
		//swaps the elements at positions i and j
		//Time Complexity:: O(1)
		//Space Complexity:: O(1)
		
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	static void Heapify(int[] arr, int n, int i, Graph G, boolean occurrence, int[] size){
		//This function converts an array into a heap
		//Time Complexity:: O(log N)    where N = size of the list to be heapifies provided the subtrees are heaps
		//Space Complexity:: O(1)
		
		//n is the size of the array and i is the root of the tree to be heapified
		int min = i;    //define a temporary integer to contain the minimum one
		int left = 2*i + 1;    //index of left child
		int right = 2*i + 2;    //index of right child
		if(left < n && !G.isLess(G.vertices.get(arr[min]), G.vertices.get(arr[left]), occurrence, size)){
			min = left;    //if the left element is smaller, store it's index (in array) in min
		}
		if(right < n && !G.isLess(G.vertices.get(arr[min]), G.vertices.get(arr[right]), occurrence, size)){
			min = right;    //if the right element is smaller, store it's index (in array) in min
		}
		//min contains the index out of (i, left, right) that has the smallest value
		if(min != i){
			//if min is not i, then one of the children of i is smaller than it
			//swap and continue the heapify procedure recursively
			Heap.swap(arr, i, min);
			Heap.Heapify(arr, n, min, G, occurrence, size);
		}
		
		//the loop runs log n times and has complexity O(1) for each stage within loop
		//T(n) = sum(c, 1, n) = O(log n)
	}
	
	static void sort(int[] arr, Graph G, int n, boolean occurrence, int[] size){
		//This function sorts an array by heapsort algorithm
		//Time Complexity:: O(n log n)
		//Space Complexity:: O(1)
		
		//Build a heap out of the array
		for(int m = n/2 - 1; m >= 0; m--){Heapify(arr, n, m, G, occurrence, size);}
		//Keep on extracting the minimum element at each stage and heapify
		for(int m = n - 1; m > 0; m--){
			Heap.swap(arr, 0, m);    //swap the maximum element with the mth (last element of the heap)
			Heapify(arr, m, 0, G, occurrence, size);    //heapify the remaining heap
		}
		//This procedure returns the reverse sorted array as required
		
		//the loop runs n times and has complexity log i for each stage
		//T(n) = sum(log i, 1, n) = O(n * log n)
	}
}