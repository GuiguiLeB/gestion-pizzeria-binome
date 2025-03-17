package com.accenture.service;


import com.accenture.exception.IngredientException;
import com.accenture.repository.Ingredient;
import com.accenture.repository.IngredientDao;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.mapper.IngredientMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceImplTest {

    @InjectMocks
    private IngredientServiceImpl service;

    @Mock
    private IngredientDao daoMock;

    @Mock
    IngredientMapper mapperMock;


    @Test
    void testAjouterNull(){
        IngredientRequestDto dto = new IngredientRequestDto("tomate",1);
        assertThrows(IngredientException.class, ()-> service.ajouter(null));
    }


    @Test
    void testNomNull(){
        IngredientRequestDto ingredient = new IngredientRequestDto(null,1);
        IngredientException ie = assertThrows(IngredientException.class, ()-> service.ajouter(ingredient));
        assertEquals("Le nom de l'ingrédient ne doit pas être null ou blank" , ie.getMessage());
    }

    @Test
    void testNomBlank(){
        IngredientRequestDto ingredient = new IngredientRequestDto("\n",1);

        IngredientException ie = assertThrows(IngredientException.class, ()-> service.ajouter(ingredient));
        assertEquals("Le nom de l'ingrédient ne doit pas être null ou blank" , ie.getMessage());
    }



    @Test
    void testQuantiteNeg(){
        IngredientRequestDto ingredient = new IngredientRequestDto("tomate",-1);
        IngredientException ie = assertThrows(IngredientException.class, ()-> service.ajouter(ingredient));
        assertEquals("L'ingrédient ne peut pas avoir une valeur négative" , ie.getMessage());
    }

    @Test
    void testAjouterOk(){

        IngredientRequestDto requestDto = new IngredientRequestDto("tomate",1);
        Ingredient tomate = getTomate();
        Ingredient tomateApresEnreg = getTomate();
        tomateApresEnreg.setId(1);
        IngredientResponseDto responseDto = new IngredientResponseDto(1,"tomate",1);

        Mockito.when(mapperMock.toIngredient(requestDto)).thenReturn(tomate);
        Mockito.when(daoMock.save(tomate)).thenReturn(tomateApresEnreg);
        Mockito.when(mapperMock.toIngredientResponseDto(tomateApresEnreg)).thenReturn(responseDto);


        assertSame(responseDto,service.ajouter(requestDto));
        Mockito.verify(daoMock).save(tomate);

    }
    @Test
    void trouverTous(){

        Ingredient tomate = getTomate();
        Ingredient mozza = new Ingredient("mozza",3);
        IngredientResponseDto tomateResp = new IngredientResponseDto(1,"tomate",1);
        IngredientResponseDto mozzaResp = new IngredientResponseDto(2,"mozza",3);

        List<Ingredient> ingredients = List.of(tomate,mozza);
        List<IngredientResponseDto> dtos = List.of(tomateResp,mozzaResp);

        Mockito.when(daoMock.findAll()).thenReturn((ingredients));
        Mockito.when(mapperMock.toIngredientResponseDto(tomate)).thenReturn(tomateResp);
        Mockito.when(mapperMock.toIngredientResponseDto(mozza)).thenReturn(mozzaResp);

        assertEquals(dtos,service.trouverTous());

    }
    @Test
    void trouverParNomPasOk(){
        Mockito.when(daoMock.findById(77)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, ()-> service.trouver(77));
        assertEquals("ingrédient non présent", ex.getMessage());

    }

    @Test
    void trouverParNomOk(){
        Ingredient ingredient = getTomate();
        Optional<Ingredient> optIngredient = Optional.of(ingredient);
        Mockito.when(daoMock.findById(1)).thenReturn(optIngredient);

        IngredientResponseDto dto = new IngredientResponseDto(1,"tomate",1);
        Mockito.when((mapperMock.toIngredientResponseDto(ingredient))).thenReturn(dto);

        assertSame(dto, service.trouver(1));

    }












    private static Ingredient getTomate() {
        return new Ingredient("tomate", 1);
    }
}
