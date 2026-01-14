package com.sparta.cafeorderback.service;

import com.sparta.cafeorderback.dto.ChargeRequest;
import com.sparta.cafeorderback.dto.PointInfoResponse;
import com.sparta.cafeorderback.entity.PointHistory;
import com.sparta.cafeorderback.entity.Users;
import com.sparta.cafeorderback.exception.CustomException;
import com.sparta.cafeorderback.exception.ErrorCode;
import com.sparta.cafeorderback.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UsersService usersService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = new Users("testuser", "testuser@test.com", "password");
    }

    @Test
    @DisplayName("포인트 충전 성공 테스트")
    void chargeMyPoint_Success() {
        // given
        String userEmail = "testuser@test.com";
        long chargeAmount = 10000L;
        ChargeRequest request = new ChargeRequest(chargeAmount);
        long initialPoints = testUser.getPoint();

        given(usersService.findUserByEmail(userEmail)).willReturn(testUser);

        // when
        PointInfoResponse response = pointService.chargeMyPoint(userEmail, request);

        // then
        // 1. 반환값 검증
        assertNotNull(response);
        assertEquals(chargeAmount, response.getAmount());
        assertEquals(initialPoints + chargeAmount, response.getCurrentPoint());

        // 2. 상호작용 검증
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));

        // 3. 상태 변화 검증
        assertEquals(initialPoints + chargeAmount, testUser.getPoint());
    }

    @Test
    @DisplayName("포인트 충전 실패 테스트 - 0원 충전 시도")
    void chargeMyPoint_Fail_ZeroAmount() {
        // given
        String userEmail = "testuser@test.com";
        long chargeAmount = 0L;
        ChargeRequest request = new ChargeRequest(chargeAmount);

        given(usersService.findUserByEmail(userEmail)).willReturn(testUser);
        
        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            pointService.chargeMyPoint(userEmail, request);
        });
        
        assertEquals(ErrorCode.INVALID_ARGUMENT, exception.getErrorCode());
        
        // 실패 시에는 save가 호출되지 않아야 함
        verify(pointHistoryRepository, times(0)).save(any(PointHistory.class));
    }
}
