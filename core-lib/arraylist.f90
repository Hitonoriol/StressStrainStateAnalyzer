module arraylist
USE, INTRINSIC :: ISO_C_BINDING
implicit none

type list_t
  integer :: n, width
  real(C_FLOAT), allocatable :: data(:)
end type

real, parameter :: GROW_FACTOR = 1.5

contains

! Create a `list_t` instance & preallocate a set amount of elements
function mklist(size)
  type(list_t) :: mklist
  integer :: size
  mklist%n = 0
  mklist%width = 1
  allocate(mklist%data(size))
end function

! Create a `list_t` instance as a pseudo-2d array of size `height` * `width`
function mkmatrix(height, width)
  type(list_t) :: mkmatrix
  integer :: height, width
  mkmatrix%n = 0
  allocate(mkmatrix%data(width * height))
  mkmatrix%width = width
end function

! Get a value from `matrix` as if it were a 2d array of width `width`
function get_2d(matrix, i, j)
  integer :: i, j
  real(C_FLOAT) :: get_2d
  type(list_t) :: matrix
  get_2d = matrix%data(j + i * matrix%width)
end function

! Check the size of `list` and grow the underlying array if needed
subroutine check_size(list)
  type(list_t) :: list
  real(C_FLOAT), allocatable :: temporary(:)

  if(.not.allocated(list%data)) then
    allocate(list%data(10))
    list%n = 0
  else if(list%n >= size(list%data)) then
    allocate(temporary(GROW_FACTOR * list%n))
    temporary(1:list%n) = list%data(1:list%n)
    call move_alloc(temporary, list%data)
  endif
end subroutine

! Append a new element to `list`
subroutine append(list, elem)
  type(list_t) :: list
  real(C_FLOAT) :: elem
  
  call check_size(list)
  list%n = list%n + 1
  list%data(list%n) = elem
  write(*,*) 'Appending ', list%data(list%n), 'at idx: ', list%n
end subroutine

! Add all elements from array `arr` to `list`
subroutine add_all(list, arr)
  type(list_t) :: list
  real(C_FLOAT) :: arr(:)
  integer :: i
  do i = 1, size(arr)
    call append(list, arr(i))
  end do
end subroutine

! Extract the underlying data array from `list` as a targetable array (can be pointed to by a pointer)
! `list%data` becomes unallocated as a result of this operation

end module