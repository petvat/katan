# Hexlib

The hexagon grid system uses 2 coordinates. Each point is represented by two values, *a* and *b*.
Using the pointy top layout, moving from _W_ to _E_ increments _a_. Moving from  _NW_ to _SE_ increments _b_.

To account for computations on intersection and edge coordinates, each hexagon coordinate must be doubled.

### Compute the edge linking two intersections:

Given 2 intersection points _A (a<sub>1</sub>, a<sub>2</sub>)_  and _B (b<sub>1</sub>, b<sub>2</sub>)_, if the
denominating value is **odd**:

- The edge will be (a<sub>1</sub> - 1, a<sub>2</sub>) if a<sub>1</sub> > a<sub>2</sub> else opposite.

**even**:

- The edge will be equal to the intersection with highest summed value.

### Compute the intersection linking two or more edges

Given 2 (or more) edge points _A (a<sub>1</sub>, a<sub>2</sub>)_  and _B (b<sub>1</sub>, b<sub>2</sub>)_,

If **a<sub>1</sub> = b<sub>1</sub>**:

- If the shared value is the highest (of a<sub>1</sub>, a<sub>2</sub>, b<sub>1</sub>, b<sub>2</sub>):
    - Intersection is equal to (c<sub>1</sub> + 1, c<sub>2</sub>), where C is the max(A, B).
- Else:
    - Intersection is the max(A, B)

If **a<sub>2</sub> = b<sub>2</sub>**:

- If the shared value is the highest:
    - Intersection is equal to (c<sub>1</sub>, c<sub>2</sub> + 1), where C is the max(A, B).
- Else:
    - Intersection is the max(A, B)

Else:
Intersection is the max(A, B)

### Compute the hexes adjacent to an intersection









