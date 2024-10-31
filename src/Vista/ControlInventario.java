/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;


import com.mysql.jdbc.PreparedStatement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;



/**
 *
 * @author Andres Gordillo
 */
public class ControlInventario extends javax.swing.JFrame {

    /**
     * Creates new form ControlInventario
     */
    public ControlInventario() {
        initComponents();
        setLocationRelativeTo(null);
        Data(jLabel2);
        mostrarDatosEnTabla();
        TablaDatosVentas();
        actualizarInventario();
    
       
        
    }

    //------------------------------------------------------------------------//

 private void mostrarDatosTablaEntrada() {
    System.out.println("MostrarEntradas");
    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("Producto");
    modelo.addColumn("Stock_Anterior");
    modelo.addColumn("Stock_Actual");
    modelo.addColumn("Cant_Entrada");

    jTable4.setModel(modelo); // Refrescar la tabla

    DefaultTableModel modeloJTable1 = (DefaultTableModel) jTable1.getModel(); // Obtener el modelo de jTable1

    // Verificar si jTable1 tiene registros
    if (modeloJTable1.getRowCount() == 0) {
        System.out.println("jTable1 no contiene registros.");
        return; // Salir si no hay datos
    }

    // Usar un Set para evitar productos duplicados
    Set<String> productosUnicos = new HashSet<>();

    // Llenar el modelo con los datos de jTable1
    for (int i = 0; i < modeloJTable1.getRowCount(); i++) {
        String producto = modeloJTable1.getValueAt(i, 0).toString(); // Producto
        String stockActual = modeloJTable1.getValueAt(i, 1).toString(); // Stock_Actual

        if (productosUnicos.add(producto)) { // Agrega si no existe en el Set
            int stockAnteriorBD = obtenerStockAnterior(producto); // Obtener Stock_Anterior de la base de datos
            int stockActualBD = Integer.parseInt(stockActual); // Stock Actual del modelo
            int cantEntrada = stockActualBD - stockAnteriorBD; // Calcular Cant_Entrada

            // Agregar el registro independientemente de Cant_Entrada
            modelo.addRow(new Object[]{producto, stockAnteriorBD, stockActualBD, cantEntrada});
            System.out.println("Producto: " + producto + ", Stock Anterior: " + stockAnteriorBD + ", Stock Actual: " + stockActualBD + ", Cant Entrada: " + cantEntrada);
        }
    }

    System.out.println("Total de filas en modelo después de agregar: " + modelo.getRowCount());

    if (modelo.getRowCount() == 0) {
        System.out.println("No se encontraron registros para mostrar en la tabla.");
    } else {
        // Guardar los datos en la base de datos
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_papeleria", "root", "")) {
            guardarDatosEnTablaEntrada(con, modelo); // Pasar la conexión válida
        } catch (SQLException e) {
            System.err.println("Error al establecer la conexión: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al establecer la conexión: " + e.getMessage());
        }
    }
}


private int obtenerStockAnterior(String producto) {
    String sqlSelect = "SELECT Stock_Anterior FROM tablaentrada WHERE Producto = ?";
    int stockAnterior = 0; // Valor por defecto si no se encuentra

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_papeleria", "root", "");
         PreparedStatement pstSelect = (PreparedStatement) con.prepareStatement(sqlSelect)) {
        pstSelect.setString(1, producto);
        ResultSet rs = pstSelect.executeQuery();
        if (rs.next()) {
            stockAnterior = rs.getInt("Stock_Anterior");
            System.out.println("Stock anterior para " + producto + ": " + stockAnterior); // Debug
        } else {
            System.out.println("No se encontró stock anterior para: " + producto); // Debug
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener el Stock_Anterior: " + e.getMessage());
    }
    
    return stockAnterior;
}

private void guardarDatosEnTablaEntrada(Connection con, DefaultTableModel modelo) { 
    String sqlUpdate = "UPDATE tablaentrada SET Stock_Anterior = ?, Stock_Actual = ?, Cant_Entrada = ? WHERE Producto = ?";
    String sqlInsert = "INSERT INTO tablaentrada (Producto, Stock_Anterior, Stock_Actual, Cant_Entrada) VALUES (?, ?, ?, ?)";

    try (PreparedStatement pstUpdate = (PreparedStatement) con.prepareStatement(sqlUpdate);
         PreparedStatement pstInsert = (PreparedStatement) con.prepareStatement(sqlInsert)) {

        for (int i = 0; i < modelo.getRowCount(); i++) {
            String producto = modelo.getValueAt(i, 0).toString();   // Producto
            int stockAnterior = Integer.parseInt(modelo.getValueAt(i, 1).toString()); // Stock_Anterior
            int stockActual = Integer.parseInt(modelo.getValueAt(i, 2).toString()); // Stock_Actual
            int cantEntrada = Integer.parseInt(modelo.getValueAt(i, 3).toString()); // Cant_Entrada

            // Actualizar en la base de datos
            pstUpdate.setInt(1, stockActual); // Actualizar Stock_Anterior con el Stock_Actual
            pstUpdate.setInt(2, stockActual); // Nuevo Stock_Actual
            pstUpdate.setInt(3, cantEntrada); // Nueva Cant_Entrada
            pstUpdate.setString(4, producto);
            pstUpdate.executeUpdate();
            System.out.println("Producto actualizado: " + producto + ", Nueva Cant_Entrada: " + cantEntrada);
        }

        //JOptionPane.showMessageDialog(null, "Datos guardados correctamente en tablaentrada.");
    } catch (SQLException e) {
        System.err.println("Error al guardar los datos: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "Error al guardar los datos: " + e.getMessage());
    }
}


//----------------------------------------------------------------------------------//

    
  public void mostrarDatosEnTabla() {
    // Definimos las columnas de la tabla
    String[] columnas = {"ID", "FechaHora", "Categoria", "Distribuidor", "Producto", "Cantidad", "Costo_Unitario", "Precio_Venta", "Total_Gasto", "Total_Venta", "Descripcion"};
    DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas);

    String sql = "SELECT * FROM tabla1";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_papeleria", "root", "");
         java.sql.Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(sql)) {

        // Iteramos sobre los resultados de la consulta
        while (rs.next()) {
            String[] fila = {
                rs.getString("ID"),
                rs.getString("FechaHora"),
                rs.getString("Categoria"),
                rs.getString("Distribuidor"),
                rs.getString("Producto"),
                rs.getString("Cantidad"),
                rs.getString("Costo"),
                rs.getString("Precio_Venta"),
                rs.getString("Total_Gasto"),
                rs.getString("Total_Venta"),
                rs.getString("Descripcion")
            };

            // Añadimos la fila al modelo
            modeloTabla.addRow(fila);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + e.getMessage());
    }

    // Asignamos el modelo de la tabla a jTable3
    jTable3.setModel(modeloTabla);
    sumarColumnas();  // Método para sumar columnas, si está implementado
}

   
 public void sumarTable1(){
    
        System.out.println("SumarTabla1");
        sumarCantVent();
        sumarPrecVent();
        sumarTotalVent();
  }
  
   public void sumarTable2(){
    
       System.out.println("SumarTabla2");
        sumarCantInvt();
        sumarTotalGastoInvt();
        sumarTotalInvt();
  }
  
  
   public void sumarColumnas(){
       
       System.out.println("sumarColumnas");
               
        sumarCantVent();
        sumarPrecVent();
        sumarTotalVent();
        
        sumarCantInvt();
        sumarTotalGastoInvt();
        sumarTotalInvt();
    
    }  
  
  public void TablaDatosVentas() {
    // Definimos las columnas de la tabla
    String[] columnas = {"Fecha", "Categoria", "Producto", "Tipo_Pago", "Cantidad", "Precio_Venta", "Total_Venta", "Descripcion"};
    DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas);

    String sql = "SELECT Fecha, Categoria, Producto, Tipo_Pago, Cantidad, Precio_Venta, Total_Venta, Descripcion FROM tablaventas";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_papeleria", "root", "");
         java.sql.Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(sql)) {

        // Iteramos sobre los resultados de la consulta
        while (rs.next()) {
            String[] fila = {
                rs.getString("Fecha"),
                rs.getString("Categoria"),
                rs.getString("Producto"),
                rs.getString("Tipo_Pago"),
                rs.getString("Cantidad"),
                rs.getString("Precio_Venta"),
                rs.getString("Total_Venta"),
                rs.getString("Descripcion")
            };

            // Añadimos la fila al modelo
            modeloTabla.addRow(fila);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + e.getMessage());
    }

    // Asignamos el modelo de la tabla a jTable2
    jTable2.setModel(modeloTabla);
    sumarColumnas();  // Método para sumar columnas, si está implementado
}

  
//--------------------------------------------------------------------

  public void actualizarInventario() {
    System.out.println("ActualizarInvent");
    // Columnas de la tabla de inventario
    String[] columnas = {"Producto", "Stock_Inicial", "Fecha_Entrada", "Fecha_Salida", "Cant_Salida"};
    DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas);

    // Consultas SQL
    final String SQL_PRODUCTOS = "SELECT Producto, Cantidad AS Stock_Inicial, FechaHora FROM tabla1";
    final String SQL_VENTAS = "SELECT Producto, SUM(Cantidad) AS Cantidad, MAX(Fecha) AS Fecha FROM tablaventas GROUP BY Producto";
    final String SQL_ACTUALIZAR = "UPDATE tablainvent SET Stock_Inicial = ?, Fecha_Salida = ?, Cant_Salida = ? WHERE Producto = ?";
    final String SQL_INSERTAR = "INSERT INTO tablainvent (Producto, Stock_Inicial, Fecha_Entrada, Fecha_Salida, Cant_Salida) VALUES (?, ?, ?, ?, ?)";

    // HashMaps para almacenar datos
    Map<String, Integer> stockInicialMap = new HashMap<>();
    Map<String, String> fechaEntradaMap = new HashMap<>();
    Map<String, Integer> salidasMap = new HashMap<>();
    Map<String, String> fechaSalidaMap = new HashMap<>();

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_papeleria", "root", "");
         PreparedStatement pstProductos = (PreparedStatement) con.prepareStatement(SQL_PRODUCTOS);
         PreparedStatement pstVentas = (PreparedStatement) con.prepareStatement(SQL_VENTAS);
         PreparedStatement pstActualizar = (PreparedStatement) con.prepareStatement(SQL_ACTUALIZAR);
         PreparedStatement pstInsertar = (PreparedStatement) con.prepareStatement(SQL_INSERTAR)) {

        // Obtener productos de 'tabla1'
        try (ResultSet rsProductos = pstProductos.executeQuery()) {
            while (rsProductos.next()) {
                String producto = rsProductos.getString("Producto");
                int cantidadInicial = rsProductos.getInt("Stock_Inicial");
                String fechaEntrada = rsProductos.getString("FechaHora");

                stockInicialMap.put(producto, cantidadInicial);
                fechaEntradaMap.put(producto, fechaEntrada);
            }
        }

        // Obtener ventas de 'tablaventas'
        try (ResultSet rsVentas = pstVentas.executeQuery()) {
            while (rsVentas.next()) {
                String producto = rsVentas.getString("Producto");
                int cantidadVendida = rsVentas.getInt("Cantidad");
                String fechaSalida = rsVentas.getString("Fecha");

                salidasMap.put(producto, cantidadVendida);
                fechaSalidaMap.put(producto, fechaSalida);
            }
        }

        // Llenar el modelo de tabla y actualizar la base de datos
        for (String producto : stockInicialMap.keySet()) {
            int stockInicial = stockInicialMap.get(producto);
            int cantSalida = salidasMap.getOrDefault(producto, 0);
            String fechaSalida = fechaSalidaMap.getOrDefault(producto, "Sin ventas");

            // Agregar fila al modelo de tabla (solo con las columnas solicitadas)
            String[] fila = {
                producto,
                String.valueOf(stockInicial),
                fechaEntradaMap.get(producto),
                fechaSalida,
                String.valueOf(cantSalida)
            };
            modeloTabla.addRow(fila);

            // Actualizar la base de datos
            pstActualizar.setInt(1, stockInicial);
            pstActualizar.setString(2, fechaSalida);
            pstActualizar.setInt(3, cantSalida);
            pstActualizar.setString(4, producto);

            int filasActualizadas = pstActualizar.executeUpdate();
            if (filasActualizadas == 0) {
                // Insertar un nuevo registro si no se encontró
                pstInsertar.setString(1, producto);
                pstInsertar.setInt(2, stockInicial);
                pstInsertar.setString(3, fechaEntradaMap.get(producto));
                pstInsertar.setString(4, fechaSalida);
                pstInsertar.setInt(5, cantSalida);
                pstInsertar.executeUpdate();
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar el inventario: " + e.getMessage());
    }

    // Actualiza la tabla en la interfaz gráfica
    jTable1.setModel(modeloTabla);
}


  
//---------------------- Tabla Venta---------------------------------||
    public void sumarCantVent() {
    
       int totalCantidad = 0;

    // Iterar sobre las filas de la tabla
    for (int i = 0; i < jTable2.getRowCount(); i++) {
        // Obtener el valor de la columna Cantidad (índice 3)
        String cantidadStr = jTable2.getValueAt(i, 4).toString();
        
        // Eliminar los puntos de miles y convertir a entero
        cantidadStr = cantidadStr.replace(".", "");
        int cantidad = Integer.parseInt(cantidadStr);
        
        // Sumar el valor
        totalCantidad += cantidad;
    }

    // Mostrar el resultado en jLabel4 con formato de miles
    jLabel4.setText("Cantidad Productos: "+ String.format("%,d ",totalCantidad).replace(",", "."));

}
    
 public void sumarPrecVent() {
    
       int totalCantidad = 0;

    // Iterar sobre las filas de la tabla
    for (int i = 0; i < jTable2.getRowCount(); i++) {
        // Obtener el valor de la columna Cantidad (índice 3)
        String cantidadStr = jTable2.getValueAt(i, 5).toString();
        System.out.println("Cantidad:"+cantidadStr);
        // Eliminar los puntos de miles y convertir a entero
        cantidadStr = cantidadStr.replace(".", "");
        int cantidad = Integer.parseInt(cantidadStr);
        
        // Sumar el valor
        totalCantidad += cantidad;
    }

    // Mostrar el resultado en jLabel4 con formato de miles
    jLabel5.setText("Precio Ventas: "+ String.format("%,d ",totalCantidad).replace(",", "."));

}

  public void sumarTotalVent() {
    
       int totalCantidad = 0;

    // Iterar sobre las filas de la tabla
    for (int i = 0; i < jTable2.getRowCount(); i++) {
        // Obtener el valor de la columna Cantidad (índice 3)
        String cantidadStr = jTable2.getValueAt(i, 6).toString();
        
        // Eliminar los puntos de miles y convertir a entero
        cantidadStr = cantidadStr.replace(".", "");
        int cantidad = Integer.parseInt(cantidadStr);
        
        // Sumar el valor
        totalCantidad += cantidad;
    }

    // Mostrar el resultado en jLabel4 con formato de miles
    jLabel3.setText("Total ventas: "+ String.format("%,d ",totalCantidad).replace(",", "."));

}
 
  //---------------------- Tabla Inventario ---------------------------------||
  
     public void sumarCantInvt() {
    
       int totalCantidad = 0;

    // Iterar sobre las filas de la tabla
    for (int i = 0; i < jTable3.getRowCount(); i++) {
        // Obtener el valor de la columna Cantidad (índice 3)
        String cantidadStr = jTable3.getValueAt(i, 5).toString();
        
        // Eliminar los puntos de miles y convertir a entero
        cantidadStr = cantidadStr.replace(".", "");
        int cantidad = Integer.parseInt(cantidadStr);
        
        // Sumar el valor
        totalCantidad += cantidad;
    }

    // Mostrar el resultado en jLabel4 con formato de miles
    jLabel7.setText("Cantidad Productos: "+ String.format("%,d ",totalCantidad).replace(",", "."));

}
    
 

  public void sumarTotalGastoInvt() {
    
       int totalCantidad = 0;

    // Iterar sobre las filas de la tabla
    for (int i = 0; i < jTable3.getRowCount(); i++) {
        // Obtener el valor de la columna Cantidad (índice 3)
        String cantidadStr = jTable3.getValueAt(i, 8).toString();
        
        // Eliminar los puntos de miles y convertir a entero
        cantidadStr = cantidadStr.replace(".", "");
        int cantidad = Integer.parseInt(cantidadStr);
        
        // Sumar el valor
        totalCantidad += cantidad;
    }

    // Mostrar el resultado en jLabel4 con formato de miles
    jLabel8.setText("Total Gasto: "+ String.format("%,d ",totalCantidad).replace(",", "."));

}
 
 
  public void sumarTotalInvt() {
    
       int totalCantidad = 0;

    // Iterar sobre las filas de la tabla
    for (int i = 0; i < jTable3.getRowCount(); i++) {
        // Obtener el valor de la columna Cantidad (índice 3)
        String cantidadStr = jTable3.getValueAt(i, 9).toString();
        
        // Eliminar los puntos de miles y convertir a entero
        cantidadStr = cantidadStr.replace(".", "");
        int cantidad = Integer.parseInt(cantidadStr);
        
        // Sumar el valor
        totalCantidad += cantidad;
    }

    // Mostrar el resultado en jLabel4 con formato de miles
    jLabel6.setText("Total ventas: "+ String.format("%,d ",totalCantidad).replace(",", "."));

}
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Control de Inventarios");

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Vista/image/hacia-atras.png"))); // NOI18N
        jButton3.setText("Volver Atras");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder("Buscar dato:"));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel2.setText("Hora&Fecha");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estadistica Venta:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Total Venta:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 204, 0));
        jLabel4.setText("Total Cantidad:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 102, 255));
        jLabel5.setText("Total Gastos:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estadistica Salida:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Stock_Inicial", "Fecha_Entrada", "Fecha_Salida", "Cant_Salida"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla venta:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "Categoria", "Producto", "Tipo_Pago", "Cantidad", "Total_Venta", "Descripcion"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(204, 204, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla inventario:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Categoria", "Producto", "Cantidad", "Total_Venta", "Descripcion"
            }
        ));
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estadistica Inventario:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel5MouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Total Venta:");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 204, 0));
        jLabel7.setText("Total Cantidad:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 102, 255));
        jLabel8.setText("Total Gastos:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estadistica Entrada:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Stock_Inicial", "Cant_Entrada"
            }
        ));
        jTable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable4MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable4);

        jButton1.setText("Calcular entradas");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(107, 107, 107))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addGap(144, 144, 144)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addComponent(jLabel1))
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(89, 89, 89)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jButton3))
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(66, 66, 66))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(12, 12, 12)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
            Menu menu = new Menu();
        menu.setVisible(true); // Mostrar el nuevo frame
        this.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked

        int selectedRow = jTable2.getSelectedRow();

        // Verificar si se ha seleccionado alguna fila
        if (selectedRow != -1) {
            // Obtener el valor de cada columna (0 a 3) de la fila seleccionada
            String valorColumna1 = jTable2.getValueAt(selectedRow, 1).toString();
            String valorColumna2 = jTable2.getValueAt(selectedRow, 2).toString();
            String valorColumna3 = jTable2.getValueAt(selectedRow, 3).toString();
            String valorColumna4 = jTable2.getValueAt(selectedRow, 4).toString();
            String valorColumna5 = jTable2.getValueAt(selectedRow, 5).toString();

        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable2MouseClicked

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        int selectedRow = jTable3.getSelectedRow();

        // Verificar si se ha seleccionado alguna fila
        if (selectedRow != -1) {
            // Obtener el valor de cada columna (0 a 3) de la fila seleccionada
            String valorColumna0 = jTable3.getValueAt(selectedRow, 0).toString();
            String valorColumna1 = jTable3.getValueAt(selectedRow, 1).toString();
            String valorColumna2 = jTable3.getValueAt(selectedRow, 2).toString();
            String valorColumna3 = jTable3.getValueAt(selectedRow, 3).toString();
            String valorColumna4 = jTable3.getValueAt(selectedRow, 4).toString();

        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable3MouseClicked

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        
        String textoBusqueda = jTextField1.getText();

    // Obtiene el modelo de la tabla
    DefaultTableModel modelo3 = (DefaultTableModel) jTable1.getModel();
    DefaultTableModel modelo2 = (DefaultTableModel) jTable2.getModel();
    DefaultTableModel modelo = (DefaultTableModel) jTable3.getModel();
     

    // Configura el TableRowSorter para el modelo de la tabla
    
    TableRowSorter<DefaultTableModel> sorter3 = new TableRowSorter<>(modelo3);
    jTable1.setRowSorter(sorter3);
    
    TableRowSorter<DefaultTableModel> sorter2 = new TableRowSorter<>(modelo2);
    jTable2.setRowSorter(sorter2);
    
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
    jTable3.setRowSorter(sorter);

    // Aplica un filtro para buscar en todas las columnas
    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusqueda));
    sorter2.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusqueda));
    sorter3.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusqueda));

    sumarColumnas();
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
        sumarTable1();        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel2MouseClicked

    private void jPanel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MouseClicked
        sumarTable2();        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel5MouseClicked

    private void jTable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable4MouseClicked
        mostrarDatosTablaEntrada();            // TODO add your handling code here:
    }//GEN-LAST:event_jTable4MouseClicked

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        mostrarDatosTablaEntrada();        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     mostrarDatosTablaEntrada();           // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

      private String calcularTotalGasto(String cantidadStr, String costoStr) {
    // Convertir el costo quitando las comas
    String costoSinComa = costoStr.replace(".", "").replace(",", ".");
    double cantidad = Double.parseDouble(cantidadStr);
    double costo = Double.parseDouble(costoSinComa);

    // Calcular el total
    double totalGasto = cantidad * costo;

    // Devolver el total gasto con formato de miles
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    return decimalFormat.format(totalGasto);
}
    
private String calcularTotalVenta(String precioVentaStr, String cantidadStr) {
    // Convertir el precio de venta quitando las comas
    String precioVentaSinComa = precioVentaStr.replace(".", "").replace(",", ".");
    double cantidad = Double.parseDouble(cantidadStr);
    double precioVenta = Double.parseDouble(precioVentaSinComa);

    // Calcular el total
    double totalVenta = cantidad * precioVenta;

    // Devolver el total venta con formato de miles
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    return decimalFormat.format(totalVenta);
}
        
    
       public static void Data(JLabel jLabel2) {
        // Crear un formateador de fecha y hora en formato de 12 horas con segundos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a | dd/MM/yyyy");

        // Definir una tarea que actualiza el JLabel
        ActionListener actualizarHora = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener la hora y fecha actual del sistema
                LocalDateTime now = LocalDateTime.now();
                String formattedDateTime = now.format(formatter);

                // Actualizar el texto de jLabel2
                jLabel2.setText(formattedDateTime);
            }
        };

        // Crear un Timer que se dispare cada 1000 ms (1 segundo)
        Timer timer = new Timer(1000, actualizarHora);

        // Ejecutar inmediatamente la primera actualización
        actualizarHora.actionPerformed(null);

        // Iniciar el Timer
        timer.start();
    }
    /**
     * @param args the command line arguments
     */
    public void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ControlInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControlInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControlInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControlInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControlInventario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
