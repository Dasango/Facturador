package com.uce.emprendimiento.backend;

import com.uce.emprendimiento.backend.sri.SriResponse;
import com.uce.emprendimiento.backend.sri.SriService;
import com.uce.emprendimiento.backend.sri.SriController; // Supongamos que testeamos el controller

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // JUNIT 5
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // MOCKITO
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// 1. @ExtendWith(MockitoExtension.class) -> Esto reemplaza a @SpringBootTest
// Le dice a JUnit: "No cargues Spring, solo carga la herramienta de Mocks".
@ExtendWith(MockitoExtension.class)
class FacturacionUnitTest {


    @Mock
    SriService sriServiceMock;

    @InjectMocks
    SriController sriController; 

    @Test
    void verificarQueElControllerRespondeBien() {

        SriResponse respuestaFalsaExitosa = new SriResponse();
        respuestaFalsaExitosa.setEstado("RECIBIDA");
        respuestaFalsaExitosa.setMensajes(null);

        when(sriServiceMock.enviarAlSri(anyString())).thenReturn(respuestaFalsaExitosa);

        ResponseEntity<SriResponse> respuesta = sriController.procesarFactura("<xml>dummy</xml>");

        assertEquals(200, respuesta.getStatusCodeValue());
        
        assertEquals("RECIBIDA", respuesta.getBody().getEstado());

        verify(sriServiceMock, times(1)).enviarAlSri(anyString());
        
        System.out.println("✅ Test Unitario pasó: "+ respuestaFalsaExitosa );
    }
}