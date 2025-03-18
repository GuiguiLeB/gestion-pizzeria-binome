package com.accenture.service;

import com.accenture.exception.IngredientException;
import com.accenture.repository.Ingredient;
import com.accenture.repository.IngredientDao;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.IngredientResponseDto;
import com.accenture.service.mapper.IngredientMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceImplTest {

    @InjectMocks
    private IngredientServiceImpl service;

    @Mock
    private IngredientDao daoMock;

    @Mock
    IngredientMapper mapperMock;


    @Test
    void testAjouterNull() {
        IngredientRequestDto dto = new IngredientRequestDto("tomate", 1);
        assertThrows(IngredientException.class, () -> service.ajouter(null));
    }


    @Test
    void testNomNull() {
        IngredientRequestDto ingredient = new IngredientRequestDto(null, 1);
        IngredientException ie = assertThrows(IngredientException.class, () -> service.ajouter(ingredient));
        assertEquals("Le nom de l'ingrédient ne doit pas être null ou blank", ie.getMessage());
    }

    @Test
    void testNomBlank() {
        IngredientRequestDto ingredient = new IngredientRequestDto("\n", 1);

        IngredientException ie = assertThrows(IngredientException.class, () -> service.ajouter(ingredient));
        assertEquals("Le nom de l'ingrédient ne doit pas être null ou blank", ie.getMessage());
    }


    @Test
    void testQuantiteNeg() {
        IngredientRequestDto ingredient = new IngredientRequestDto("tomate", -1);
        IngredientException ie = assertThrows(IngredientException.class, () -> service.ajouter(ingredient));
        assertEquals("L'ingrédient ne peut pas avoir une valeur négative", ie.getMessage());
    }

    @Test
    void testAjouterOk() {

        IngredientRequestDto requestDto = new IngredientRequestDto("tomate", 1);
        Ingredient tomate = getTomate();
        Ingredient tomateApresEnreg = getTomate();
        tomateApresEnreg.setId(1);
        IngredientResponseDto responseDto = getTomateResp();

        when(mapperMock.toIngredient(requestDto)).thenReturn(tomate);
        when(daoMock.save(tomate)).thenReturn(tomateApresEnreg);
        when(mapperMock.toIngredientResponseDto(tomateApresEnreg)).thenReturn(responseDto);


        assertSame(responseDto, service.ajouter(requestDto));
        verify(daoMock).save(tomate);

    }

    @Test
    void trouverTous() {

        Ingredient tomate = getTomate();
        Ingredient mozza = getMozza();
        IngredientResponseDto tomateResp = getTomateResp();
        IngredientResponseDto mozzaResp = getMozzaResp();

        List<Ingredient> ingredients = List.of(tomate, mozza);
        List<IngredientResponseDto> dtos = List.of(tomateResp, mozzaResp);

        when(daoMock.findAll()).thenReturn((ingredients));
        when(mapperMock.toIngredientResponseDto(tomate)).thenReturn(tomateResp);
        when(mapperMock.toIngredientResponseDto(mozza)).thenReturn(mozzaResp);

        assertEquals(dtos, service.trouverTous());

    }

    @Test
    void trouverParNomPasOk() {
        when(daoMock.findById(77)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.trouver(77));
        assertEquals("ingrédient non présent", ex.getMessage());

    }

    @Test
    void trouverParNomOk() {
        Ingredient ingredient = getTomate();
        Optional<Ingredient> optIngredient = Optional.of(ingredient);
        when(daoMock.findById(1)).thenReturn(optIngredient);

        IngredientResponseDto dto = getTomateResp();
        when((mapperMock.toIngredientResponseDto(ingredient))).thenReturn(dto);

        assertSame(dto, service.trouver(1));

    }

    @Test
    void modifierSiIdNonPresent() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.modifier(1, getTomateRequest()));
        assertEquals("ingrédient non présent", ex.getMessage());
    }

    @Test
    void modifierQuantiteIngredientExistant() {
        // GIVEN - Préparation des données
        Ingredient ingredientAvantModification = new Ingredient();
        ingredientAvantModification.setId(1);
        ingredientAvantModification.setNom("Tomate");
        ingredientAvantModification.setQuantite(5);

        IngredientRequestDto ingredientRequestDto = new IngredientRequestDto("Tomate", 10);

        Ingredient ingredientApresModification = new Ingredient();
        ingredientApresModification.setId(1);
        ingredientApresModification.setNom("Tomate");
        ingredientApresModification.setQuantite(10);

        IngredientResponseDto expectedResponse = new IngredientResponseDto(1, "Tomate", 10);

        // WHEN - Définition des comportements mockés
        when(daoMock.findById(1)).thenReturn(Optional.of(ingredientAvantModification));
        when(daoMock.save(ingredientAvantModification)).thenReturn(ingredientApresModification);
        when(mapperMock.toIngredientResponseDto(ingredientApresModification)).thenReturn(expectedResponse);

        // ACT - Appel de la méthode à tester
        IngredientResponseDto result = service.modifier(1, ingredientRequestDto);

        // THEN - Vérifications
        assertNotNull(result);
        assertEquals(expectedResponse.quantite(), result.quantite());

        // Vérifier que `save` a bien été appelé avec l'ingrédient modifié
        verify(daoMock).save(ingredientAvantModification);
    }


    @Test
    void modifierNomIngredientExistant() {
        // GIVEN - Préparation des données
        Ingredient ingredientAvantModification = new Ingredient();
        ingredientAvantModification.setId(1);
        ingredientAvantModification.setNom("Tomate");
        ingredientAvantModification.setQuantite(10);

        IngredientRequestDto ingredientRequestDto = new IngredientRequestDto("Tomate", 10);

        Ingredient ingredientApresModification = new Ingredient();
        ingredientApresModification.setId(1);
        ingredientApresModification.setNom("Tomates");
        ingredientApresModification.setQuantite(10);

        IngredientResponseDto expectedResponse = new IngredientResponseDto(1, "Tomates", 10);

        // WHEN - Définition des comportements mockés
        when(daoMock.findById(1)).thenReturn(Optional.of(ingredientAvantModification));
        when(daoMock.save(ingredientAvantModification)).thenReturn(ingredientApresModification);
        when(mapperMock.toIngredientResponseDto(ingredientApresModification)).thenReturn(expectedResponse);

        // ACT - Appel de la méthode à tester
        IngredientResponseDto result = service.modifier(1, ingredientRequestDto);

        // THEN - Vérifications
        assertNotNull(result);
        assertEquals(expectedResponse.nom(), result.nom());

        // Vérifier que `save` a bien été appelé avec l'ingrédient modifié
        verify(daoMock).save(ingredientAvantModification);
    }


    @Test
    void modifierIngredientAvecNomNull() {
            IngredientRequestDto dto = new IngredientRequestDto(null, 1);
            assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
        }


    @Test
    void modifierIngredientAvecQuantiteNeg2() {
        IngredientRequestDto dto = new IngredientRequestDto(null, 11);
        assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
    }

    @Test
    void modifierIngredientAvecQuantiteNull2() {
        IngredientRequestDto dto = new IngredientRequestDto("Tomate", null);
        assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
    }

    @Test
    void modifierIngredientAvecQuantiteNeg() {
        // GIVEN
        IngredientRequestDto dto = new IngredientRequestDto("Tomate", -11);

        // WHEN & THEN
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.modifier(1, dto));
        assertEquals("ingrédient non présent", ex.getMessage());
    }

    @Test
    void modifierIngredientAvecQuantiteNull() {
        // GIVEN
        Ingredient ingredientAvantModification = new Ingredient();
        ingredientAvantModification.setId(1);
        ingredientAvantModification.setNom("Tomate");
        ingredientAvantModification.setQuantite(5);

        IngredientRequestDto dto = new IngredientRequestDto("Tomate", null);

        IngredientResponseDto expectedResponse = new IngredientResponseDto(1, "Tomate", 5);

        // WHEN
        when(daoMock.findById(1)).thenReturn(Optional.of(ingredientAvantModification));
        when(daoMock.save(ingredientAvantModification)).thenReturn(ingredientAvantModification);
        when(mapperMock.toIngredientResponseDto(ingredientAvantModification)).thenReturn(expectedResponse);

        // ACT
        IngredientResponseDto result = service.modifier(1, dto);

        // THEN
        assertNotNull(result);
        assertEquals(5, result.quantite());  // La quantité doit rester inchangée
        verify(daoMock).save(ingredientAvantModification);
    }


//************************************************************************************************************************
//                                                  METHODES PRIVEES
//************************************************************************************************************************
//

    private static IngredientResponseDto getMozzaResp() {
        return new IngredientResponseDto(2, "mozza", 3);
    }

    private static IngredientResponseDto getTomateResp() {
        return new IngredientResponseDto(1, "tomate", 1);
    }

    private static Ingredient getMozza() {
        return new Ingredient("mozza", 3);
    }

    private static Ingredient getTomate() {
        return new Ingredient("tomate", 1);
    }

    private static IngredientRequestDto getTomateRequest() {
        return new IngredientRequestDto("tomate", 1);
    }

}
