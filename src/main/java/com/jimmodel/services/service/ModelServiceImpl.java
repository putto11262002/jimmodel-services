package com.jimmodel.services.service;

import com.jimmodel.services.domain.BaseEntity;
import com.jimmodel.services.domain.Model;
import com.jimmodel.services.domain.Image;
import com.jimmodel.services.exception.ResourceNotFoundException;
import com.jimmodel.services.exception.ValidationException;
import com.jimmodel.services.repository.ImageRepository;
import com.jimmodel.services.repository.ModelRepository;
import com.jimmodel.services.util.StorageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.File;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ModelServiceImpl implements ModelService{
    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ImageRepository modelImageRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    Validator validator;

    private final Integer COMP_CARD_IMAGE_LIMIT = 10;

    private final String PROFILE_IMAGE_PREFIX = "profile";

    private final String COMP_CARD_IMAGE_PREFIX = "compcard";

    private final String MODEL_IMAGE_DIR_NAME = "model";

    @Override
    public Model save(Model model){
        Set<ConstraintViolation<BaseEntity>> violations = validator.validate(model);
        if(!violations.isEmpty()){
            throw new ValidationException(String.join(",",  violations.stream().map(violation -> violation.getMessage()).collect(Collectors.toList())));
        }
        return modelRepository.save(model);
    }

    @Override
    public Model findById(UUID id) {
        return modelRepository.findById(id)
                .orElseThrow(() ->  new ResourceNotFoundException(String.format("Model with id %s does not exist.", id)));
    }

    @Override
    public Model saveById(UUID id, Model updatedModel) {
        Model model = modelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Model with id %s does not exist.", id)));

        model.setFirstName(updatedModel.getFirstName());
        model.setLastName(updatedModel.getLastName());
        model.setOtherNames(updatedModel.getOtherNames());
        model.setEmail(updatedModel.getEmail());
        model.setPhoneNumber(updatedModel.getPhoneNumber());
        model.setWhatsApp(updatedModel.getWhatsApp());
        model.setWeChat(updatedModel.getWeChat());
        model.setInstagram(updatedModel.getInstagram());
        model.setFacebook(updatedModel.getFacebook());
        model.setDateOfBirth(updatedModel.getDateOfBirth());
        model.setGender(updatedModel.getGender());
        model.setNationality(updatedModel.getNationality());
        model.setEthnicity(updatedModel.getEthnicity());
        model.setCountryOfResidence(updatedModel.getCountryOfResidence());
        model.setSpokenLanguage(updatedModel.getSpokenLanguage());
        model.setPassportNumber(updatedModel.getPassportNumber());
        model.setIdCard(updatedModel.getIdCard());
        model.setTaxId(updatedModel.getTaxId());
        model.setOccupation(updatedModel.getOccupation());
        model.setEducation(updatedModel.getEducation());
        model.setAddress(updatedModel.getAddress());
        model.setTalent(updatedModel.getTalent());
        model.setMedicalBackground(updatedModel.getMedicalBackground());
        model.setTattooScar(updatedModel.getTattooScar());
        model.setUnderwearShooting(updatedModel.getUnderwearShooting());
        model.setInTown(updatedModel.getInTown());
        model.setExperiences(updatedModel.getExperiences());
        model.setMeasurement(updatedModel.getMeasurement());
        return this.save(model);
    }

    @Override
    public Page<Model> findAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return modelRepository.findAll(pageable);
    }

    @Override
    public void deleteById(UUID id) {
        if (!modelRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format("Model with id %s does not exist.", id));
        }
        modelRepository.deleteById(id);
    }


    @Override
    public Image saveCompCardImageById(UUID id, MultipartFile file) {
        if (!StorageUtil.validateImageMediaType(file)){
            throw new ValidationException("Invalid image format"); // TODO - could potentially use validation exception
        }
        Model model = modelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Model with id %s does not exist.", id)));
        if (model.getCompCardImage().size() >= this.COMP_CARD_IMAGE_LIMIT){
            throw new ValidationException("Exceeded upload limit"); // TODO - create custom exception
        }
        String fileName = String.format("%s-%s.%s",this.COMP_CARD_IMAGE_PREFIX,LocalDateTime.now().atZone(ZoneOffset.UTC), file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        storageService.save(this.MODEL_IMAGE_DIR_NAME  + File.separatorChar + model.getId(), fileName, file);
        Image compCard = Image.builder()
                .type(MediaType.parseMediaType(file.getContentType()))
                .fileName(fileName)
                .build();
        model.addCompCardImage(compCard);
        modelRepository.save(model);
        return compCard;
    }

    @Override
    public Image saveProfileImageById(UUID id, MultipartFile file) {
        if (!StorageUtil.validateImageMediaType(file)){
            throw new javax.validation.ValidationException("Invalid image format"); // TODO - could potentially use validation exception
        }
        Model model = modelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Model with id %s does not exist.", id)));

        String newFileName =  String.format("%s-%s.%s", this.PROFILE_IMAGE_PREFIX, LocalDateTime.now().atZone(ZoneOffset.UTC), file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        storageService.save(this.MODEL_IMAGE_DIR_NAME + File.separatorChar + model.getId(),  newFileName, file);
        if(model.getProfileImage() != null) { // if not null delete before store new one
            storageService.delete(model.getProfileImage().getFileName());
        }

        Image profileImage =
                Image.builder()
                        .type(MediaType.parseMediaType(file.getContentType()))
                        .fileName(newFileName)
                        .build();
        model.setProfileImage(profileImage);
        modelRepository.save(model);
        return profileImage;
    }

    @Override
    public void deleteCompCardImageByImageId(UUID id) {
        Image image = modelImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Model image with id %s does not exist.", id)));
        storageService.delete(image.getFileName());
    }

    @Override
    public void deleteProfileImageById(UUID id) {
        Model model = modelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Model with id %s does not exist.", id)));
        storageService.delete(model.getProfileImage().getFileName());
        model.setProfileImage(null);
    }

    @Override
    public Resource getImage(UUID id, String fileName) {
        return storageService.load(this.MODEL_IMAGE_DIR_NAME + File.separatorChar + id + File.separatorChar + fileName);
    }

    @Override
    public Page<Model> search(String searchTerm, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return modelRepository.search(searchTerm, pageable);
    }

    @Override
    public void publish(UUID id) {
        Model model = this.findById(id);
        model.setPublished(true);
        this.modelRepository.save(model);
    }

    @Override
    public void unpublish(UUID id) {
        Model model = this.findById(id);
        model.setPublished(false);
        this.modelRepository.save(model);
    }

    @Override
    public Page<Model> findAllPublished(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return this.modelRepository.findAllByPublished(true, pageable);
    }
}
