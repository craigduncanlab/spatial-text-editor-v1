/* This is a custom iterator for the Definition objects
{currently not needed:23 Nov 2017}
but it attempts to implement Java's ListIterator interface
actually, Java's LinkedList is a more concrete implementation
If you implement certain lists you can generalise about them as a COllection, then utilise
Java's methods for Collections (it's a kind of bottom-up object orientation)
Specifically, "Classes Like ArrayList and LinkedList...have a built in iterator method that returns a readyy built iterator object.
So with object orientation, you call the method, obtain the object (iterator) that has the methods you want, 
then call the methods on that object to suit your purposes
*/
public class DefinitionIterator implements ListIterator<Definition>() {
	
//empty iterator	
public DefinitionIterator() {

}

}