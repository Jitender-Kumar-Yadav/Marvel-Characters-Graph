//import statements
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

public class MarvelGraph{
	public static void main(String[] args) throws Exception{
		Graph Marvel = new Graph();
		CSVReader.nodesRead(args[0], Marvel);
	}
}

class CSVReader{
	static void nodesRead(String name, Graph G) throws Exception{
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
			id = read.next();
			if(id.charAt(0) == '"'){
				temp = read.next();
				id = id.substring(1) + "," + temp.substring(0, temp.length() - 2);
			}
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
}

class Graph{
	//this class stores the main graph
	int size;    //integer to store number of vertices in the graph
	HashMap<String, Integer> indices;    //this hashmap stores the index for each label of a vertex
	ArrayList<Vertex> vertices;    //this list stores the vertices at correct index
	LinkedList<Edge>[] adjList;    //the list shall store the linked lists containing the adjacenct list of each vertex
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
	Vertex[] end;    //stores the integers representing end point vertices
	//Since the graph is undirected, the ordering shall not matter
	int weight;    //stores the weight of each edge
	void Edge(Vertex source, Vertex target, int weight){
		end = new Vertex[2];
		end[0] = source;
		end[1] = target;
		weight = weight;    //constructor for edge
	}
}