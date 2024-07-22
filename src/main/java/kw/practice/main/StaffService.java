package kw.practice.main;

import kw.practice.entity.Staff;
import kw.practice.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Staff getStaffById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    public Staff saveOrUpdateStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
    }

}
