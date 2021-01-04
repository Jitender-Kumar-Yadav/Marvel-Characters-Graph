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
		System.out.println(Marvel.size + " " + Marvel.edgesize);
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
				id = id.substring(1) + "," + temp.substring(0, temp.length() - 2);
			}
			//reading the label
			label = read.next();
			if(label.charAt(0) == '"'){
				temp = read.next();
				label = label.substring(1) + "," + temp.substring(0, temp.length() - 2);
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
				source = source.substring(1) + "," + temp.substring(0, temp.length() - 2);
			}
			//reading the target label
			target = read.next();
			if(target.charAt(0) == '"'){
				temp = read.next();
				target = target.substring(1) + "," + temp.substring(0, temp.length() - 2);
			}
			//reading the weight
			weight = Integer.parseInt(read.next());
			v1 = G.vertices.get(G.indices.get(source));    //store the source vertex
			v2 = G.vertices.get(G.indices.get(target));    //store the target vertex
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
	void Graph(){}
}

class Vertex{
	//objects of this class are vertices of the main Graph
	String id;    //stores the id of the character
	String label;    //stores the label of the character
	int index;    //stores a unique indext for that vertex
	Vertex(String id, String label, int index){
		id = id;
		label = label;
		index = index;    //constructor for vertex
	}
}

class Edge{
	//objects of this class are edges of the main Graph
	//these shall be stored in adjacency list
	Vertex[] end;    //stores the vertices representing end points of the edge
	//Since the graph is undirected, the ordering shall not matter
	//nonetheless the source shall be the current vertex in whose adjacency list the edge is added
	int weight;    //stores the weight of each edge
	Edge(Vertex source, Vertex target, int weight){
		end = new Vertex[2];
		end[0] = source;
		end[1] = target;
		weight = weight;    //constructor for edge
	}
}