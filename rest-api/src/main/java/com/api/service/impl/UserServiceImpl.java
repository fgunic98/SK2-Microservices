package com.api.service.impl;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.dto.TokenRequestDto;
import com.api.dto.TokenResponseDto;
import com.api.dto.UserCreateDto;
import com.api.dto.UserDto;
import com.api.exeptions.NotFoundException;
import com.api.mapper.UserMapper;
import com.api.model.User;
import com.api.repository.UserRepository;
import com.api.service.UserService;
import com.api.tokenService.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private TokenService tokenService;
	private UserRepository userRepository;
	private UserMapper userMapper;
	
	


	public UserServiceImpl(TokenService tokenService, UserRepository userRepository, UserMapper userMapper) {
		super();
		this.tokenService = tokenService;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public Page<UserDto> findAll(Pageable pageeable) {
		return userRepository.findAll(pageeable).map(userMapper::userToUserDto);
	}
	
	public User findById(String string) {
		if(userRepository.findById(string).isPresent()) return userRepository.findById(string).get();
		else return null;
	}

	@Override
	public UserDto add(UserCreateDto userCreateDto) {
		User user = userMapper.userCreateDtoToUser(userCreateDto);
		userRepository.save(user);
		return userMapper.userToUserDto(user);
	}

	@Override
	public TokenResponseDto login(TokenRequestDto tokenRequestDto) {
		User user = userRepository.findByUsernameAndPassword(tokenRequestDto.getUsername(), tokenRequestDto.getPassword())
				.orElseThrow(() -> new NotFoundException(String.format("User with username %s and password %s not found.", tokenRequestDto.getUsername(),tokenRequestDto.getPassword())));
		Claims claims = Jwts.claims();
		claims.put("id",user.getId());
		claims.put("role",user.getRole());
		return new TokenResponseDto(tokenService.generate(claims));
	}
}
