package Vista;

import Modelo.Nota;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.*;


public class Gestor extends JFrame{

    // Variables globales de la clase
    private static JTextField campoTitulo;
    private static JTextArea campoTexto;
    private static Nota nota;
    private static JList<Nota> listaNotas;
    private static DefaultListModel<Nota> modeloDeNotas;
    private static Nota notaAnterior = null;
    private static boolean eliminarNota = false;
    private static JFrame frame;
    

    public Gestor() {
        modeloDeNotas = new DefaultListModel<>();
        listaNotas = new JList<>(modeloDeNotas);

        frame = new JFrame("Gestor de notas"); // Crear la ventana principal
        frame.setSize(800, 300);
        frame.setLocationRelativeTo(null); // Centrar la ventana
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(); // Crear un panel
        frame.add(panel);

        colocarCampoTexto(panel);
        colocarBotones(panel);
        frame.setVisible(true);
    }

    

    // Métodos para colocar los componentes de texto en el panel
    private static void colocarCampoTexto(JPanel panel) {
        JPanel panelCampoTexto = new JPanel();
        panelCampoTexto.setLayout(new BorderLayout());
        campoTitulo = new JTextField("Título de la nota");
        panelCampoTexto.add(campoTitulo, BorderLayout.NORTH);
        campoTexto = new JTextArea(10, 30); // 10 filas, 30 columnas
        campoTexto.setLineWrap(true); // activa el salto de línea automático
        campoTexto.setWrapStyleWord(true); // activa el salto de línea en palabras enteras
        panelCampoTexto.add(campoTexto, BorderLayout.CENTER);
        panel.add(panelCampoTexto, BorderLayout.NORTH);
    }
    
    // Métodos para colocar los botones en el panel y sus acciones
    private static void colocarBotones(JPanel panel) {

        // Crear un panel para los botones con una GridLayout de 2 filas y 3 columnas
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(2, 3)); 
        

        // Crear el boton de guardar nota
        JButton botonGuardar = new JButton("Guardar nota");
        panelBotones.add(botonGuardar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        botonGuardar.addActionListener(new ActionListener() { // Implementar el ActionListener del boton
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String titulo = campoTitulo.getText();
                String texto = campoTexto.getText();

                // Verificar que los campos no estén vacíos
                if (titulo.isEmpty() || texto.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
                    return;
                }

                // Verificar si ya existe una nota con el mismo título
                for (int i = 0; i < modeloDeNotas.getSize(); i++) {
                    Nota nota = (Nota) modeloDeNotas.getElementAt(i);
                    if (nota.getTitulo().equals(titulo)) {
                        JOptionPane.showMessageDialog(null, "Ya existe una nota con el título '" + titulo + "'.");
                        return;
                    }
                }

                // Obtener el correo del usuario actual
                String correo = Login.getCorreo();
        

                // Crear el archivo con el nombre del correo
                String archivo = "src/Datos/usuarios/" + correo + ".txt";

        

                try {
                    FileWriter writer = new FileWriter(archivo, true);
                    writer.write("---\n");
                    writer.write(titulo + "\n");
                    writer.write(texto + "\n");
                    writer.close();
                    JOptionPane.showMessageDialog(null, "Nota guardada correctamente en el archivo " + archivo);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar la nota en el archivo " + archivo);
                }

                // Añadir la nueva nota a la lista
                nota = new Nota(titulo, texto);
                modeloDeNotas.addElement(nota);
                JOptionPane.showMessageDialog(null, "Nota guardada correctamente en el modelo");
                

                // Limpiar los campos
                campoTexto.setText("");
                campoTitulo.setText("");
            }
        });

    

    // Crear el boton de listar notas
    JButton botonListar = new JButton("Listar notas");
    panelBotones.add(botonListar);
    panel.add(panelBotones, BorderLayout.SOUTH);
    botonListar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // Obtener el correo del usuario actual
            String correo = Login.getCorreo();
    
            // Crear el archivo con el nombre del correo
            String archivo = "src/Datos/usuarios/" + correo + ".txt";

            // Crear un modelo de lista para almacenar las notas
            DefaultListModel<Nota> modeloDeNotas = new DefaultListModel<>();
            modeloDeNotas.clear();
    
            try {
                // Leer el archivo de texto
                File file = new File(archivo);
                Scanner scanner = new Scanner(file);
    
                // Crear un modelo de lista para almacenar los títulos
                DefaultListModel<String> modeloDeTitulos = new DefaultListModel<>();
    
                // Leer las notas del archivo
                String titulo = "";
                String contenido = "";
                boolean esTitulo = true;
                while (scanner.hasNextLine()) {
                    String linea = scanner.nextLine();
                    if (linea.equals("---")) {
                        // Añadir el título a la lista
                        modeloDeTitulos.addElement(titulo);
                        titulo = "";
                        contenido = "";
                        esTitulo = true;
                    } else if (esTitulo) {
                        titulo = linea;
                        esTitulo = false;
                    }else {
                        contenido += linea + "\n";
                    }
                }
                // Recoger la última nota
                if (!titulo.isEmpty()) {
                    modeloDeTitulos.addElement(titulo);
                }
                scanner.close();
    
                // Crear una nueva ventana con la lista de títulos
                JFrame ventanaNotas = new JFrame("Notas de " + correo);
                ventanaNotas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventanaNotas.setSize(600, 400);
                ventanaNotas.setLocationRelativeTo(null); 
    
                // Crear una lista con los títulos
                JList<String> listaTitulos = new JList<>(modeloDeTitulos);
                listaTitulos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
                // Agregar un ListSelectionListener a la lista de títulos
                // Agregar un MouseListener a la lista de notas
                listaNotas.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int indice = listaNotas.locationToIndex(e.getPoint());
                        if (indice != -1) {
                            Nota nota = (Nota) listaNotas.getModel().getElementAt(indice);
                            String titulo = nota.getTitulo();
                            // Buscar el contenido correspondiente en el archivo .txt
                            String contenido = buscarContenido(titulo);
                            // Mostrar el contenido de la nota en una ventana nueva
                            JFrame ventanaContenido = new JFrame("Contenido de la nota");
                            ventanaContenido.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            ventanaContenido.setSize(400, 300);
                            ventanaContenido.setLocationRelativeTo(null);
                            JTextArea textoContenido = new JTextArea(contenido);
                            textoContenido.setEditable(false);
                            ventanaContenido.add(new JScrollPane(textoContenido));
                            ventanaContenido.setVisible(true);
                        }
                    }
                });
    
                // Agregar la lista a la ventana
                ventanaNotas.add(new JScrollPane(listaTitulos));
    
                // Mostrar la ventana
                ventanaNotas.setVisible(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error al leer el archivo " + archivo);
            }
        }
    });

        // Crear el boton de limpiar campo de texto
        JButton botonLimpiar = new JButton("Limpiar campo de texto");
        panelBotones.add(botonLimpiar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        botonLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                campoTexto.setText("");
                campoTitulo.setText("");
            }
        });



        // Crear el boton de buscar nota
        JButton botonBuscar = new JButton("Buscar nota");
        panelBotones.add(botonBuscar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        botonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Buscador de Notas");
                dialog.setModal(true);
        
                JLabel labelBusqueda = new JLabel("Ingrese el título de la nota a buscar:");
                dialog.getContentPane().add(labelBusqueda, BorderLayout.NORTH);
        
                JTextArea textoBusqueda = new JTextArea(1, 30); 
                textoBusqueda.setLineWrap(true); 
                textoBusqueda.setWrapStyleWord(true);
        
                JPanel panelBusqueda = new JPanel();
                panelBusqueda.setLayout(new BorderLayout());
                panelBusqueda.add(labelBusqueda, BorderLayout.NORTH);
                panelBusqueda.add(new JScrollPane(textoBusqueda), BorderLayout.CENTER);
        
                JButton botonBuscarNota = new JButton("Buscar");
                botonBuscarNota.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        String busqueda = textoBusqueda.getText();
                        
                        String contenido = buscarContenido(busqueda);
                        if (contenido != null) {
                            JDialog ventanaContenido = new JDialog(frame, true); // Establecer la ventana principal como padre
                            ventanaContenido.setTitle("Contenido de la nota");
                            ventanaContenido.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            ventanaContenido.setSize(400, 300);
                            ventanaContenido.setLocationRelativeTo(null); // Centrar la ventana en la pantalla
                            JTextArea textoContenido = new JTextArea(contenido);
                            textoContenido.setEditable(false);
                            ventanaContenido.add(new JScrollPane(textoContenido));
                            ventanaContenido.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Nota no encontrada");
                        }
                    }
                });
        
                panelBusqueda.add(botonBuscarNota, BorderLayout.SOUTH);
                dialog.add(panelBusqueda);
                dialog.setSize(800, 300);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });


        // Crear el boton de eliminar nota
        JButton botonEliminar = new JButton("Eliminar nota");
        panelBotones.add(botonEliminar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        botonEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Código que te proporcioné anteriormente
                String correo = Login.getCorreo();
                String archivo = "src/Datos/usuarios/" + correo + ".txt";
                DefaultListModel<Nota> modelo = (DefaultListModel<Nota>) listaNotas.getModel();
                modelo.clear();
                
                try {
                    File file = new File(archivo);
                    Scanner scanner = new Scanner(file);
                    String titulo = "";
                    while (scanner.hasNextLine()) {
                        String linea = scanner.nextLine();
                        if (!linea.equals("---")) {
                            if (titulo.isEmpty()) {
                                titulo = linea;
                                modelo.addElement(new Nota(titulo));
                            } else {
                                // Lee el contenido de la nota
                                String contenido = "";
                                while (scanner.hasNextLine() && !scanner.nextLine().equals("---")) {
                                    contenido += scanner.nextLine() + "\n";
                                }
                                // Agrega el contenido a la nota correspondiente en el modelo de lista
                                Nota nota = (Nota) modelo.getElementAt(modelo.getSize() - 1);
                                nota.setTexto(contenido);
                                titulo = "";
                            }
                        }
                    }
                    scanner.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al leer el archivo");
                }
        
                JDialog ventanaEliminar = new JDialog(frame, true);
                ventanaEliminar.setTitle("Eliminar Nota");
                ventanaEliminar.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                ventanaEliminar.setSize(400, 300);
                ventanaEliminar.setLocationRelativeTo(null);
        
                JList<Nota> listaNotasEliminar = new JList<>();
                listaNotasEliminar.setModel(modelo);
                listaNotasEliminar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
                JButton botonEliminarNota = new JButton("Eliminar");
                botonEliminarNota.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        int indice = listaNotasEliminar.getSelectedIndex();
                        if (indice != -1) {
                            Nota nota = listaNotasEliminar.getModel().getElementAt(indice);
                            String titulo = nota.getTitulo();
                            eliminarNota(titulo, archivo);
                            modelo.remove(indice);
                        }
                    }
                });
        
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(new JScrollPane(listaNotasEliminar), BorderLayout.CENTER);
                panel.add(botonEliminarNota, BorderLayout.SOUTH);
        
                ventanaEliminar.add(panel);
                ventanaEliminar.setVisible(true);
            }
        });
                    

        // Agregar el botón "Salir"
        JButton botonSalir = new JButton("Cerrar sesión");
        panelBotones.add(botonSalir);
        panel.add(panelBotones, BorderLayout.SOUTH);
        botonSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                frame.dispose();
                Login.setUsuario();
                Login.setContrasenna();
            }
        });
    }
    

// Método para buscar el contenido correspondiente en el archivo .txt
private static String buscarContenido(String titulo) {
    String correo = Login.getCorreo();
    String archivo = "src/Datos/usuarios/" + correo + ".txt";
    try {
        File file = new File(archivo);
        Scanner scanner = new Scanner(file);
        boolean encontrado = false;
        String contenido = "";
        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine();
            if (linea.equals(titulo)) {
                encontrado = true;
            } else if (encontrado && !linea.equals("---")) {
                contenido += linea + "\n";
            } else if (encontrado && linea.equals("---")) {
                break;
            }
        }
        scanner.close();
        return contenido.trim(); // Eliminar espacios en blanco al final
    } catch (IOException ex) {
        return "Error al leer el archivo";
    }
}

private static void eliminarNota(String titulo, String archivo) {
    try {
        File file = new File(archivo);
        Scanner scanner = new Scanner(file);
        StringBuilder contenido = new StringBuilder();
        boolean encontrado = false;
        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine();
            if (linea.equals(titulo)) {
                encontrado = true;
            } else if (encontrado && linea.equals("---")) {
                encontrado = false;
            } else if (!encontrado) {
                contenido.append(linea).append("\n");
            }
        }
        scanner.close();
        FileWriter writer = new FileWriter(file);
        writer.write(contenido.toString());
        writer.close();
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, "Error al eliminar la nota");
    }
}
}
