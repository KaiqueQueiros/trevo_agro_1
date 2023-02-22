package trevo.agro.api.product;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import trevo.agro.api.category.Category;
import trevo.agro.api.category.CategoryRepository;
import trevo.agro.api.culture.Culture;
import trevo.agro.api.culture.CultureRepository;
import trevo.agro.api.utils.ResponseModel;
import trevo.agro.api.utils.ResponseModelEspec;
import trevo.agro.api.utils.ResponseModelEspecNoObject;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CultureRepository cultureRepository;

    public ResponseEntity<ResponseModel> register(@Valid ProductDTO dto) {
        try {
            List<Culture> cultures = cultureRepository.findByIdIn(dto.cultureIds());
            List<Category> categories = categoryRepository.findByIdIn(dto.categoryIds());
            if (cultures.isEmpty() || categories.isEmpty()) {
                return new ResponseEntity<>(new ResponseModelEspecNoObject("Categoria/Cultura não encontrada"), HttpStatus.NOT_FOUND);
            }
            Product product = new Product(dto, categories, cultures);
            repository.save(product);
            return new ResponseEntity<>(new ResponseModelEspec("Produto foi salvo", dto), HttpStatus.OK);
        } catch (Exception error) {
            error.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();

    }

    public ResponseEntity<ResponseModel> list() {
        List<Product> products = repository.findAll();
        if (products.isEmpty()) {
            return new ResponseEntity<>(new ResponseModelEspecNoObject("Não existem produtos cadastrados"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseModelEspec("Aqui esta a lista de produtos", products), HttpStatus.OK);
    }

    public ResponseEntity<ResponseModel> details(@PathVariable Long id) {
        Optional<Product> products = repository.findById(id);
        if (products.isEmpty()) {
            return new ResponseEntity<>(new ResponseModelEspecNoObject("Não existe produto com este id."), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseModelEspec("Aqui esta os detalhes deste produto", products), HttpStatus.OK);
    }

    public ResponseEntity<ResponseModel> delete(@PathVariable Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(new ResponseModelEspecNoObject("Produto excluido"), HttpStatus.OK);
        } catch (Error error) {
            error.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

    public ResponseEntity<ResponseModel> update(@Valid ProductDTO dto,@PathVariable Long id) {

        try {
            var cultures = cultureRepository.findByIdIn(dto.cultureIds());
            var categories = categoryRepository.findByIdIn(dto.categoryIds());
            if (cultures.isEmpty() || categories.isEmpty()) {
                return new ResponseEntity<>(new ResponseModelEspecNoObject("Categoria/Cultura não encontrada"), HttpStatus.NOT_FOUND);
            }
            Product productExists = repository.findById(id).orElse(null);
            if (productExists == null) {
                return ResponseEntity.notFound().build();
            }
            productExists.update(dto,categories,cultures);
            repository.save(productExists);
            return new ResponseEntity<>(new ResponseModelEspecNoObject("Produto foi atualizado!"), HttpStatus.OK);
        } catch (Exception error) {
            error.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }


}