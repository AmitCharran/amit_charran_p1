# Amit Charran Project 1

### There are two java projects included in these folders.
### There is an ORM and a Servlet
##### The ORM is packaged into a Jar and is used in the Servlet layer to generate a SQL/DAO layer

##### The servlet layer can create classes with proper annotations and pass them to the ORM layer in the Configuration class and their host, user, and password for a postgres SQL login to dynamically create SQL tables with classes

#####Classes requirements
* Proper getter/setter methods
* @Entity annotation above class name
* @Id annotation above one member field in class to identify primary key. Primary Key is required to be int
* @Column annotation above all other member fields

##### To Add class to Database
* Create a `Configuration` Object
* Then call `Configuration.addAnnotatedClass(YourClass);`
