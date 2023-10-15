package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.LotDTO;
import com.OnlineAuction.Exceptions.UnableToGenerateIdException;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.LotsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LotServices {

    private final LotsRepository lotsRepository;

    private final UserServices userServices;

    @Autowired
    public LotServices(LotsRepository lotsRepository, UserServices userServices) {
        this.lotsRepository = lotsRepository;
        this.userServices = userServices;
    }

    public Long generateId() {
        int count = 0;
        while (count < 100) {
            String uuid = String.format("%06d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
            Long id = Long.valueOf(uuid);
            Optional<Lot> lot = lotsRepository.findById(id);
            if (lot.isEmpty()) {
                return id;
            }
            count++;
        }
        throw new UnableToGenerateIdException();
    }

    public List<Lot> getLots() {
        return lotsRepository.findAll();
    }

    public Lot getLotById(Long id) {
        return lotsRepository.getReferenceById(id);
    }

    //Will be added soon
    //public Lot getLotByNameAndAuction() {}

    public Lot create(LotDTO lotDTO) {
        User creator = userServices.getUserByEmail("test");

        if (creator == null) {
            throw new EntityNotFoundException();
        }

        Long id = generateId();
        Lot newLot = new Lot(id, lotDTO, creator);
        return lotsRepository.save(newLot);
    }

    public Lot update(LotDTO lotDTO, Long id_lot) {
        Lot existLot = lotsRepository.getReferenceById(id_lot);

        if (lotDTO.name() != null) {
            existLot.setName(lotDTO.name());
        }

        if (lotDTO.description() != null) {
            existLot.setDescription(lotDTO.description());
        }

        if (lotDTO.image() != null) {
            existLot.setImage(lotDTO.image());
        }

        if (lotDTO.minimum_price() != 0) {
            existLot.setMinimum_price(lotDTO.minimum_price());
        }

        return lotsRepository.save(existLot);
    }

    public boolean delete(Long id) {
        Lot lot = lotsRepository.getReferenceById(id);
        /*if (lot == null) {
            throw new EntityNotFoundException();
        }*/

        lotsRepository.delete(lot);
        return true;
    }
}