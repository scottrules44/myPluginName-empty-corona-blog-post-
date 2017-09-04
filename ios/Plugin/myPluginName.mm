#import "myPluginName.h"

#include "CoronaAssert.h"
#include "CoronaEvent.h"
#include "CoronaLibrary.h"
#include "CoronaLuaIOS.h"
#include "CoronaRuntime.h"

class myPluginName
{
	public:
		typedef myPluginName Self;

	public:
		static const char kName[];
		static const char kEvent[];

	protected:
		myPluginName();

	public:
		bool Initialize( CoronaLuaRef listener );

	public:
		CoronaLuaRef GetListener() const { return fListener; }

	public:
		static int Open( lua_State *L );

	protected:
		static int Finalizer( lua_State *L );

	public:
		static Self *ToLibrary( lua_State *L );

	
	private:
		CoronaLuaRef fListener;
};
const char myPluginName::kName[] = "plugin.myPluginName";
const char myPluginName::kEvent[] = "myPluginName";

myPluginName::myPluginName()
:	fListener( NULL )
{
}

bool
myPluginName::Initialize( CoronaLuaRef listener )
{
	// Can only initialize listener once
	bool result = ( NULL == fListener );

	if ( result )
	{
		fListener = listener;
	}

	return result;
}

int
myPluginName::Open( lua_State *L )
{
	// Register __gc callback
	const char kMetatableName[] = __FILE__; // Globally unique string to prevent collision
	CoronaLuaInitializeGCMetatable( L, kMetatableName, Finalizer );

	// Functions in library
	const luaL_Reg kVTable[] =
	{
        

		{ NULL, NULL }
	};

	// Set library as upvalue for each library function
	Self *library = new Self;
	CoronaLuaPushUserdata( L, library, kMetatableName );

	luaL_openlib( L, kName, kVTable, 1 ); // leave "library" on top of stack
    
    
    
	return 1;
}

int
myPluginName::Finalizer( lua_State *L )
{
	Self *library = (Self *)CoronaLuaToUserdata( L, 1 );

	CoronaLuaDeleteRef( L, library->GetListener() );

	delete library;

	return 0;
}

myPluginName *
myPluginName::ToLibrary( lua_State *L )
{
	// library is pushed as part of the closure
	Self *library = (Self *)CoronaLuaToUserdata( L, lua_upvalueindex( 1 ) );
	return library;
}
static NSString *
ToNSString( lua_State *L, int index )
{
    NSString *result = nil;
    
    int t = lua_type( L, -2 );
    switch ( t )
    {
        case LUA_TNUMBER:
            result = [NSString stringWithFormat:@"%g", lua_tonumber( L, index )];
            break;
        default:
            result = [NSString stringWithUTF8String:lua_tostring( L, index )];
            break;
    }
    
    return result;
}




CORONA_EXPORT int luaopen_plugin_myPluginName( lua_State *L )
{
	return myPluginName::Open( L );
}
