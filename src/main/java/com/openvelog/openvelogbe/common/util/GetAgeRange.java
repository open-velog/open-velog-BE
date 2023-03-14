package com.openvelog.openvelogbe.common.util;

import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;

import java.util.Calendar;

public class GetAgeRange {
    public AgeRange getAge(Member member)
    {
        AgeRange ageRange;
        Calendar current = Calendar.getInstance();
        int currentYear  = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int currentDay   = current.get(Calendar.DAY_OF_MONTH);

        int birthYear = member.getBirthday().getYear();
        int birthMonth = member.getBirthday().getMonthValue();
        int birthDay = member.getBirthday().getDayOfMonth();

        int age = currentYear - birthYear;
        // 생일 안 지난 경우 -1
        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay)
            age--;

        ageRange = age<20 ? AgeRange.TO19 : age<30 ? AgeRange.TO29
                 : age<40 ? AgeRange.TO39 : age<60 ? AgeRange.TO59 : AgeRange.OVER60;

        return ageRange;
    }

}
