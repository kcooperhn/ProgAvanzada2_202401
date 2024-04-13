package hn.uth.views.empleados;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.contextmenu.GridSubMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.Component;
import hn.uth.controller.InteractorEmpleados;
import hn.uth.controller.InteractorImplEmpleados;
import hn.uth.data.Empleado;
import hn.uth.data.EmpleadosReport;
import hn.uth.data.Puesto;
import hn.uth.data.SamplePerson;
import hn.uth.services.ReportGenerator;
import hn.uth.views.MainLayout;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Empleados")
@Route(value = "empleados/:identidad?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class EmpleadosView extends Div implements BeforeEnterObserver, ViewModelEmpleados {

    private final String EMPLOYEE_ID = "identidad";
    private final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "empleados/%s/edit";

    private final Grid<Empleado> grid = new Grid<>(Empleado.class, false);

    private TextField nombre;
    private TextField apellido;
    private TextField identidad;
    private TextField telefono;
    private TextField horario;
    private ComboBox<Puesto> puesto;
    private NumberField sueldo;
    private DatePicker fechaNacimiento;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar", new Icon(VaadinIcon.CHECK_CIRCLE));
    private final Button eliminar = new Button("Eliminar", new Icon(VaadinIcon.TRASH));

    private Empleado empleadoSeleccionado;
    private List<Empleado> elementos;
    private List<Puesto> puestos;
    private InteractorEmpleados controlador;
	private Puesto puestoSeleccionado;

    public EmpleadosView() {
        addClassNames("empleados-view");
        
        controlador = new InteractorImplEmpleados(this);
        elementos = new ArrayList<>();
        puestos = new ArrayList<>();

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("identidad").setAutoWidth(true);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("apellido").setAutoWidth(true);
        grid.addColumn("telefono").setAutoWidth(true);
        grid.addColumn("horario").setAutoWidth(true);
        grid.addColumn("nombre_puesto").setAutoWidth(true).setHeader("Puesto");
        grid.addColumn("sueldo").setAutoWidth(true);
        grid.addColumn("fecha_nacimiento").setAutoWidth(true).setHeader("Fecha de Nacimiento");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, event.getValue().getIdentidad()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EmpleadosView.class);
            }
        });
        
        GridContextMenu<Empleado> menu = grid.addContextMenu();
        GridMenuItem<Empleado> assign = menu.addItem("Exportar");
        GridMenuItem<Empleado> menueliminar = menu.addItem("Eliminar");
        menueliminar.addMenuItemClickListener( e -> {
        	
        });
        
        assign.addComponentAsFirst(createIcon(VaadinIcon.FILE_REFRESH));

        GridSubMenu<Empleado> exportSubMenu = assign.getSubMenu();
        exportSubMenu.addItem("Portable Document Format (.pdf)", event -> {
        	Notification.show("Generando reporte PDF...");
        	generarReporte();
        });

        controlador.consultarEmpleados();
        controlador.consultarPuestos();

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.empleadoSeleccionado == null) {
                	//ES UNA CREACIÓN
                    this.empleadoSeleccionado = new Empleado();
                    
                    this.empleadoSeleccionado.setApellido(apellido.getValue());
                    this.empleadoSeleccionado.setHorario(horario.getValue());
                    this.empleadoSeleccionado.setIdentidad(identidad.getValue());
                    this.empleadoSeleccionado.setNombre(nombre.getValue());
                    this.empleadoSeleccionado.setSueldo(sueldo.getValue());
                    this.empleadoSeleccionado.setTelefono(telefono.getValue());
                    this.empleadoSeleccionado.setPuesto(puesto.getValue().getIdpuesto());
                    this.empleadoSeleccionado.setFecha_nacimiento(convertDateToLocal(fechaNacimiento.getValue()));
                    
                    this.controlador.crearEmpleado(empleadoSeleccionado);
                    
                }else {
                	//ES UNA ACTUALIZACIÓN
                	this.empleadoSeleccionado.setApellido(apellido.getValue());
                    this.empleadoSeleccionado.setHorario(horario.getValue());
                    this.empleadoSeleccionado.setIdentidad(identidad.getValue());
                    this.empleadoSeleccionado.setNombre(nombre.getValue());
                    this.empleadoSeleccionado.setSueldo(sueldo.getValue());
                    this.empleadoSeleccionado.setTelefono(telefono.getValue());
                    this.empleadoSeleccionado.setPuesto(puesto.getValue().getIdpuesto());
                    
                    this.controlador.actualizarEmpleado(empleadoSeleccionado);
                }
                
                clearForm();
                refreshGrid();
                UI.getCurrent().navigate(EmpleadosView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        eliminar.addClickListener( e-> {
        	if(this.empleadoSeleccionado == null) {
        		mostrarMensajeError("Seleccione un empleado para poder eliminarlo");
        	}else {
        		this.controlador.eliminarEmpleado(empleadoSeleccionado.getIdentidad());
        		 clearForm();
                 refreshGrid();
                 UI.getCurrent().navigate(EmpleadosView.class);
        	}
        });
    }
    
    private void generarReporte() {
    	ReportGenerator generador = new ReportGenerator();
    	EmpleadosReport datasource = new EmpleadosReport();
    	datasource.setEmpleados(elementos);
    	Map<String, Object> parameters = new HashMap<>();
    	if(elementos.size() % 2 == 0) {
    		parameters.put("FIRMA", "firma.png");
    	}else {
    		parameters.put("FIRMA", "firma2.png");
    	}
    	
    	boolean generado = generador.generarReportePDF("reporte_personal", parameters, datasource);
    	if(generado) {
    		String ubicacion = generador.getReportPath();
    		Anchor url = new Anchor(ubicacion, "Abrir Reporte");
    		url.setTarget("_blank");
    		
    		Notification notification = new Notification(url);
    	    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    	    notification.setPosition(Position.MIDDLE);
    	    notification.setDuration(15000);
    	    notification.open();
    	}else {
    		//OCURRIO UN ERROR
    		mostrarMensajeError("Ocurrió un problema al generar el reporte :(");
    	}
	}

	private Component createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("margin-inline-end", "var(--lumo-space-s")
                .set("padding", "var(--lumo-space-xs");
        return icon;
    }
    
    private Date convertDateToLocal(LocalDate fecha) {
		return java.sql.Date.valueOf(fecha);
	}

    //METODO QUE SE EJECUTA AL SELECCIONAR UN ELEMENTO DEL GRID
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> identidadEmpleado = event.getRouteParameters().get(EMPLOYEE_ID);
        if (identidadEmpleado.isPresent()) {
            Empleado empledoObtenido = obtenerEmpleado(identidadEmpleado.get());
            if (empledoObtenido != null) {
                populateForm(empledoObtenido);
            } else {
                Notification.show(
                        String.format("El empleado con Identidad = %s no existe", identidadEmpleado.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EmpleadosView.class);
            }
        }
    }

    private Empleado obtenerEmpleado(String identidad) {
		Empleado encontrado = null;
    	for(Empleado emp: elementos) {
			if(emp.getIdentidad().equals(identidad)) {
				encontrado = emp;
				break;
			}
		}
		return encontrado;
	}

	private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nombre = new TextField("Nombre");
        nombre.setId("txt_nombre");
        nombre.setPrefixComponent(VaadinIcon.USER_CHECK.create());
        
        
        apellido = new TextField("Apellido");
        apellido.setId("txt_apellido");
        identidad = new TextField("Identidad");
        identidad.setId("txt_identidad");
        identidad.setPrefixComponent(VaadinIcon.USER_CARD.create());
        
        
        telefono = new TextField("Telefono");
        telefono.setId("txt_telefono");
        horario = new TextField("Horario");
        horario.setId("txt_horario");
        puesto = new ComboBox<>("Puesto");
        puesto.setId("txt_puesto");
        puesto.setItemLabelGenerator(Puesto::getNombre);
        
        sueldo = new NumberField("Sueldo");
        sueldo.setId("txt_sueldo");
        sueldo.setLabel("Sueldo");
        sueldo.setValue(0.0);
        Div lempiraPrefix = new Div();
        lempiraPrefix.setText("L.");
        sueldo.setPrefixComponent(lempiraPrefix);
        
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        fechaNacimiento = new DatePicker("Fecha de Nacimiento");
        fechaNacimiento.setMin(now.plusYears(-70));
        fechaNacimiento.setMax(now.plusYears(-17));
        
        //METODO ADD (AGREGA UN CONTROL A LA PANTALLA O DENTRO DE OTRO CONTROL)
        //SI EL ADD ESTÁ SOLO, LO AGREGA DIRECTAMENTE EN LA PANTALLA
        //SI EL ADD ESTA DE ESTA FORMA {CONTROL}.ADD LO AGREGA DENTRO DE {CONTROL}
        formLayout.add(identidad, nombre, apellido, telefono, horario, puesto, sueldo, fechaNacimiento);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.setId("btn_cancelar");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setId("btn_guardar");
        eliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        eliminar.setId("btn_eliminar");
        
        
        buttonLayout.add(save, eliminar, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        this.controlador.consultarEmpleados();
        this.controlador.consultarPuestos();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Empleado value) {
        this.empleadoSeleccionado = value;
        if(value != null) {
        	nombre.setValue(value.getNombre());
            apellido.setValue(value.getApellido());
            identidad.setValue(value.getIdentidad());
            telefono.setValue(value.getTelefono());
            horario.setValue(value.getHorario());
            
            puestoSeleccionado = buscarPuesto(value.getPuesto());
            puesto.setValue(puestoSeleccionado);
            sueldo.setValue(value.getSueldo());
        }else {
        	nombre.setValue("");
            apellido.setValue("");
            identidad.setValue("");
            telefono.setValue("");
            horario.setValue("");
            puesto.clear();
            sueldo.setValue(0.0);
        }     
    }

	private Puesto buscarPuesto(int idPuesto) {
		Puesto encontrado = null;
    	for(Puesto pst: puestos) {
			if(pst.getIdpuesto() == idPuesto) {
				encontrado = pst;
				break;
			}
		}
		return encontrado;
	}

	@Override
	public void mostrarEmpleadosEnGrid(List<Empleado> items) {
		Collection<Empleado> itemsCollection = items;
		grid.setItems(itemsCollection);
		this.elementos = items;
	}

	@Override
	public void mostrarMensajeError(String mensaje) {
		Notification n = Notification.show(mensaje);
		n.setPosition(Position.MIDDLE);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
	}

	@Override
	public void mostrarPuestosEnCombobox(List<Puesto> items) {
		Collection<Puesto> itemsCollection = items;
		puestos = items;
		puesto.setItems(items);
	}

	@Override
	public void mostrarMensajeExito(String mensaje) {		
		Notification notification = new Notification();
	    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	    Icon icon = VaadinIcon.CHECK_CIRCLE.create();

	    var layout = new HorizontalLayout(icon, new Text(mensaje));
	    layout.setAlignItems(FlexComponent.Alignment.CENTER);

	    notification.add(layout);
		
	    notification.setPosition(Position.MIDDLE);
	    notification.setDuration(5000);
	    notification.open();
	}
}
