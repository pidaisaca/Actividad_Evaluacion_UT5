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

        // Crear el boton de editar nota
        JButton botonEditar = new JButton("Editar nota");
        panelBotones.add(botonEditar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        botonEditar.addActionListener(new ActionListener() { // Implementar el ActionListener del boton
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Edición de Notas. Seleccione la nota a editar.");
                dialog.setModal(true); 

                JScrollPane scrollPane = new JScrollPane(listaNotas);
                dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
                dialog.setSize(500, 300);
                dialog.setLocationRelativeTo(null);

            listaNotas.addListSelectionListener(new ListSelectionListener() {
                @Override
                    public void valueChanged(ListSelectionEvent e) {
                    Nota nota = listaNotas.getSelectedValue();
                
                    if (nota != notaAnterior && nota != null) {
                        Gestor.notaAnterior = nota;
                    
                        JDialog ventanaNota = new JDialog();
                        ventanaNota.setTitle(nota.getTitulo());
                        ventanaNota.setModal(true);
                        ventanaNota.setSize(800, 300);
                        ventanaNota.setLocationRelativeTo(null);
                        ventanaNota.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    
                        JPanel panelNota = new JPanel();
                        panelNota.setLayout(new BorderLayout());
                    
                        // Agregar un panel para el título y el texto
                        JPanel panelEditar = new JPanel();
                        panelEditar.setLayout(new BorderLayout());
                    
                        JTextField campoTituloEditar = new JTextField(nota.getTitulo());
                        panelEditar.add(campoTituloEditar, BorderLayout.NORTH);
                    
                        JTextArea campoTextoEditar = new JTextArea(nota.getTexto());
                        campoTextoEditar.setLineWrap(true);
                        campoTextoEditar.setWrapStyleWord(true);
                        panelEditar.add(new JScrollPane(campoTextoEditar), BorderLayout.CENTER);
                    
                        panelNota.add(panelEditar, BorderLayout.CENTER);
                    
                        // Agregar un botón para guardar los cambios
                        JButton botonGuardarCambios = new JButton("Guardar cambios");
                        panelNota.add(botonGuardarCambios, BorderLayout.SOUTH);
                    
                        botonGuardarCambios.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent e) { // Esta accion guarda los cambios realizados en la nota
                                String tituloNuevo = campoTituloEditar.getText();
                                String textoNuevo = campoTextoEditar.getText();
                            
                                nota.setTitulo(tituloNuevo);
                                nota.setTexto(textoNuevo);
                            
                                modeloDeNotas.setElementAt(nota, listaNotas.getSelectedIndex());
                            
                                JOptionPane.showMessageDialog(null, "Nota editada correctamente");

                                ventanaNota.dispose();

                                listaNotas.clearSelection();

                                Gestor.notaAnterior = null;
                            }
                        });
                    
                        ventanaNota.add(panelNota);
                        ventanaNota.setVisible(true);
                    }
                }
            });
        dialog.setVisible(true);
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

                JLabel labelBusqueda = new JLabel("Ingrese el titulo de la nota a buscar:");
                dialog.getContentPane().add(labelBusqueda, BorderLayout.NORTH);

                JTextArea textoBusqueda = new JTextArea(10, 30); 
                textoBusqueda.setLineWrap(true); 
                textoBusqueda.setWrapStyleWord(true);

                JPanel panelBusqueda = new JPanel();
                panelBusqueda.setLayout(new BorderLayout());
                panelBusqueda.add(labelBusqueda, BorderLayout.NORTH);
                panelBusqueda.add(new JScrollPane(textoBusqueda), BorderLayout.CENTER);

                dialog.add(panelBusqueda);
                dialog.setSize(800, 300);
                dialog.setLocationRelativeTo(null);


                JButton botonBuscar = new JButton("Buscar");
                dialog.add(botonBuscar, BorderLayout.SOUTH);

                botonBuscar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        String busqueda = textoBusqueda.getText();
                        Nota notaEncontrada = Gestor.buscarNota(busqueda); 
                        if (notaEncontrada != null) {
                            JOptionPane.showMessageDialog(null, "Se ha encontrado la nota: " + notaEncontrada.getTitulo());
                            
                        } else {
                            JOptionPane.showMessageDialog(null, "Nota no encontrada");

                        }
                    }

                    
                });
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
                eliminarNota = true;
        
                if (modeloDeNotas.getSize() > 0) {
                    // Crear una ventana nueva para listar las notas
                    JDialog ventanaNotas = new JDialog();
                    ventanaNotas.setTitle("Eliminar nota");
                    ventanaNotas.setModal(true);
                    ventanaNotas.setSize(800, 300);
        
                    // Crear un panel para listar las notas
                    JPanel panelNotas = new JPanel();
                    panelNotas.setLayout(new BorderLayout());
                    
        
                    // Crear una lista para mostrar las notas
                    JList<Nota> listaNotasEliminar = new JList<Nota>(modeloDeNotas);
                    listaNotasEliminar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
                    // Crear un panel para el botón "Eliminar"
                    JPanel panelBotonEliminar = new JPanel();
                    JButton botonEliminarNota = new JButton("Eliminar");
                    panelBotonEliminar.add(botonEliminarNota);
        
                    // Agregar el panel de la lista y el botón "Eliminar" al panel principal
                    panelNotas.add(new JScrollPane(listaNotasEliminar), BorderLayout.CENTER);
                    panelNotas.add(panelBotonEliminar, BorderLayout.SOUTH);
        
                    // Agregar el panel principal a la ventana
                    ventanaNotas.add(panelNotas);
        
                    
                    botonEliminarNota.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        int indice = listaNotasEliminar.getSelectedIndex();
                        if (indice == -1) {
                        // Si no se ha seleccionado ninguna nota, seleccionar la última nota seleccionada
                        if (notaAnterior != null) {
                            listaNotasEliminar.setSelectedValue(notaAnterior, true);
                            indice = listaNotasEliminar.getSelectedIndex();
                        }
                    }
                    if (indice != -1) {
                        // Mostrar un cuadro de diálogo de confirmación antes de eliminar la nota
                        int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar la nota seleccionada?", "Confirmación", JOptionPane.YES_NO_OPTION);
                        if (respuesta == JOptionPane.YES_OPTION) {
                        modeloDeNotas.remove(indice);
                        notaAnterior = null;
                        JOptionPane.showMessageDialog(null, "La nota seleccionada ha sido eliminada con éxito.");
                    }
                } else {
                        JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna nota.");
                    }
                }
            });
                    
        
                    // Mostrar la ventana
                    ventanaNotas.pack();
                    ventanaNotas.setLocationRelativeTo(null);
                    ventanaNotas.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "No hay notas para eliminar");
                }
            }
        });




        // Crear el boton de eliminar varias notas
        JButton botonEliminarVarios = new JButton("Eliminar varios");
        panelBotones.add(botonEliminarVarios);
        panel.add(panelBotones, BorderLayout.SOUTH);
        botonEliminarVarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JDialog dialog = new JDialog();
                dialog.setTitle("Eliminar varias notas");
                dialog.setModal(true);

        // Crear un modelo de lista que permita seleccionar varias notas a la vez
            DefaultListModel<Nota> modeloLista = new DefaultListModel<>();
            for (int i = 0; i < modeloDeNotas.getSize(); i++) {
                Nota nota = (Nota) modeloDeNotas.getElementAt(i);
                modeloLista.addElement(nota);
            }

        // Crear una lista que permita seleccionar varias notas a la vez
            JList<Nota> listaNotas = new JList<>(modeloLista);
            listaNotas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Agregar un MouseListener para permitir la selección múltiple
            listaNotas.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = listaNotas.locationToIndex(e.getPoint());
                    if (index != -1) {
                        listaNotas.addSelectionInterval(index, index);
                    }
                }
            }
        });
            JPanel panelLista = new JPanel();
            panelLista.setLayout(new BorderLayout());
            panelLista.add(new JScrollPane(listaNotas), BorderLayout.CENTER);

            JLabel etiqueta = new JLabel("Para seleccionar varias notas, pulse la tecla Shift");
            panelLista.add(etiqueta, BorderLayout.NORTH);


        // Crear un botón "Eliminar" que elimine las notas seleccionadas de la lista
        JButton botonEliminar = new JButton("Eliminar");
        botonEliminar.addActionListener(new ActionListener() {
            @Override
            /* Muestra un diálogo de confirmación para verificar si se desea
            * eliminar las notas seleccionadas.*/
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int[] indices = listaNotas.getSelectedIndices();
                if (indices.length > 0) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar las notas seleccionadas?", "Confirmación", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    for (int i = indices.length - 1; i >= 0; i--) {
                     modeloDeNotas.remove(indices[i]);
                    }
                    JOptionPane.showMessageDialog(null, "Las notas seleccionadas han sido eliminadas con éxito.");
                    dialog.dispose();
                }
                    } else {
                JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna nota.");
                }
            }
        });

        
        JPanel panelBotonEliminar = new JPanel();
        panelBotonEliminar.add(botonEliminar);

        
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.add(panelLista, BorderLayout.CENTER);
        panelPrincipal.add(panelBotonEliminar, BorderLayout.SOUTH);

        
        dialog.add(panelPrincipal);
        dialog.setSize(800, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
});

        // Agregar el botón "Salir"
        JButton botonSalir = new JButton("Salir");
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

    /** Busca una nota en la lista de notas por su título
     * devuelve la nota encontrada o null si no se encuentra
     * @param busqueda
     * @return
     * 
     */
    public static Nota buscarNota(String busqueda) {
        for (int i = 0; i < listaNotas.getModel().getSize(); i++) {
            Nota nota = (Nota) listaNotas.getModel().getElementAt(i);
            if (nota.getTitulo().equals(busqueda)) {
                return nota;
            }
        }
        return null;
    }

    // Método para buscar el contenido correspondiente en el archivo .txt
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
}
